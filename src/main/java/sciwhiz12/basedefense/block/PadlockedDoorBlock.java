package sciwhiz12.basedefense.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.IRegistryDelegate;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;
import sciwhiz12.basedefense.item.BrokenLockPiecesItem;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.item.lock.AbstractPadlockItem;
import sciwhiz12.basedefense.tileentity.LockableTile;
import sciwhiz12.basedefense.tileentity.PadlockedDoorTile;
import sciwhiz12.basedefense.util.UnlockHelper;
import sciwhiz12.basedefense.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.util.text.TextFormatting.*;
import static sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static sciwhiz12.basedefense.Reference.Capabilities.KEY;
import static sciwhiz12.basedefense.Reference.Sounds;

public class PadlockedDoorBlock extends Block {
    private static final Map<IRegistryDelegate<Block>, IRegistryDelegate<Block>> REPLACEMENT_MAP = new HashMap<>();

    public static void clearReplacements() {
        REPLACEMENT_MAP.clear();
    }

    public static PadlockedDoorBlock getReplacement(Block blockIn) {
        return (PadlockedDoorBlock) REPLACEMENT_MAP.get(blockIn.delegate).get();
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoorSide> SIDE = EnumProperty.create("padlock", DoorSide.class);
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

    public final Block baseBlock;

    public PadlockedDoorBlock() {
        this(Blocks.IRON_DOOR);
    }

    public PadlockedDoorBlock(Block blockIn) {
        super(Block.Properties.from(blockIn));
        this.baseBlock = blockIn;
        REPLACEMENT_MAP.put(blockIn.delegate, this.delegate);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(SIDE, DoorSide.OUTSIDE)
                .with(HINGE, DoorHingeSide.LEFT).with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getBlock() == this && state.get(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PadlockedDoorTile();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
            BlockRayTraceResult rayTrace) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.offset(Direction.DOWN);
            state = worldIn.getBlockState(pos);
        }
        if (worldIn.isBlockPresent(pos) && state.getBlock() == this) {
            ItemStack keyStack = player.getHeldItem(handIn);
            TileEntity te = worldIn.getTileEntity(pos);
            if (!keyStack.isEmpty() && te instanceof PadlockedDoorTile) {
                PadlockedDoorTile doorTile = (PadlockedDoorTile) te;
                if (keyStack.getCapability(KEY).isPresent()) {
                    if (allowOpen(state.get(SIDE), state.get(FACING), rayTrace.getFace())) {
                        IWorldPosCallable worldPos = Util.getOrDummy(worldIn, pos);
                        if (UnlockHelper.checkRemove(keyStack, doorTile, worldPos, player, true)) {
                            replaceDoor(worldIn, pos);
                        }
                        player.swingArm(handIn);
                        return ActionResultType.SUCCESS;
                    }
                } else if (handIn == Hand.OFF_HAND) {
                    if (player.isSneaking() && allowOpen(state.get(SIDE), state.get(FACING), rayTrace.getFace())) {
                        ItemStack lockStack = doorTile.getLockStack();
                        if (!lockStack.isEmpty() && lockStack.hasDisplayName()) {
                            player.sendStatusMessage(new TranslationTextComponent("status.basedefense.door.info",
                                            lockStack.getDisplayName().copyRaw().mergeStyle(WHITE)).mergeStyle(YELLOW,
                                    ITALIC),
                                    true);
                        }
                    } else {
                        player.sendStatusMessage(new TranslationTextComponent("status.basedefense.door.locked",
                                new TranslationTextComponent(this.baseBlock.getTranslationKey()).mergeStyle(WHITE))
                                .mergeStyle(GRAY, ITALIC), true);
                    }
                    worldIn.playSound(player, pos, Sounds.LOCKED_DOOR_ATTEMPT, SoundCategory.BLOCKS, 1.0F,
                            worldIn.rand.nextFloat() * 0.1F + 0.9F);
                }
            }
        }
        return ActionResultType.PASS;
    }

    private boolean allowOpen(DoorSide lockSide, Direction blockFacing, Direction interactFacing) {
        return lockSide == DoorSide.getSideForDirection(blockFacing, interactFacing);
    }

    private void replaceDoor(World worldIn, BlockPos pos) {
        final BlockState state = worldIn.getBlockState(pos);
        final BlockPos offPos = state.get(PadlockedDoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        final BlockState offState = worldIn.getBlockState(offPos);

        final Direction facing = state.get(FACING);
        final DoorHingeSide hinge = state.get(HINGE);
        final BlockState defState = this.baseBlock.getDefaultState().with(DoorBlock.HINGE, hinge)
                .with(DoorBlock.FACING, facing).with(DoorBlock.OPEN, false);

        final BlockState newState = defState.with(DoorBlock.HALF, state.get(HALF));
        final BlockState newOffState = defState.with(DoorBlock.HALF, offState.get(HALF));

        int flags = Constants.BlockFlags.DEFAULT_AND_RERENDER | Constants.BlockFlags.NO_NEIGHBOR_DROPS;
        worldIn.setBlockState(offPos, newOffState, flags);
        worldIn.setBlockState(pos, newState, flags);
    }

    public boolean isValidLock(ItemStack stack) {
        return stack.getItem() instanceof AbstractPadlockItem;
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
        if (!drops.isEmpty() && te != null) {
            for (ItemStack stack : drops) {
                LazyOptional<ICodeHolder> codeCap = stack.getCapability(CODE_HOLDER);
                if (!stack.isEmpty() && codeCap.isPresent()) {
                    te.getCapability(CODE_HOLDER)
                            .ifPresent(teCode -> codeCap.ifPresent(stackCode -> stackCode.setCodes(teCode.getCodes())));
                    if (te instanceof LockableTile) {
                        ItemStack lockStack = ((LockableTile) te).getLockStack();
                        if (stack.getItem() instanceof IColorable && lockStack.getItem() instanceof IColorable) {
                            IColorable from = (IColorable) lockStack.getItem();
                            IColorable to = (IColorable) stack.getItem();
                            to.setColors(stack, from.getColors(lockStack));
                        }
                        if (stack.getItem() instanceof BrokenLockPiecesItem) {
                            ((BrokenLockPiecesItem) stack.getItem()).setPreviousName(stack, lockStack.getDisplayName());
                        } else if (lockStack.hasDisplayName()) { stack.setDisplayName(lockStack.getDisplayName()); }
                    }
                    break;
                }
            }
        }
        return drops;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            case EAST:
            default:
                return EAST_AABB;
            case WEST:
                return WEST_AABB;
            case NORTH:
                return NORTH_AABB;
            case SOUTH:
                return SOUTH_AABB;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.canCollide ? state.getShape(worldIn, pos) : VoxelShapes.empty();
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.getShape(worldIn, pos);
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te,
            ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf half = state.get(HALF);
        BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.up() : pos.down();
        BlockState otherState = worldIn.getBlockState(otherPos);
        if (otherState.get(HALF) != half) {
            ItemStack itemstack = player.getHeldItemMainhand();
            if (!worldIn.isRemote && !player.isCreative() && player.func_234569_d_(otherState)) {
                BlockPos tePos = (half == DoubleBlockHalf.LOWER) ? pos : otherPos;
                Block.spawnDrops(state, worldIn, pos, worldIn.getTileEntity(tePos), player, itemstack);
                Block.spawnDrops(otherState, worldIn, otherPos, worldIn.getTileEntity(tePos), player, itemstack);
            }
            worldIn.setBlockState(otherPos, Blocks.AIR.getDefaultState(), 35);
            worldIn.playEvent(player, Constants.WorldEvents.BREAK_BLOCK_EFFECTS, otherPos, Block.getStateId(otherState));
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return blockstate.isSolidSide(worldIn, blockpos, Direction.UP);
        } else {
            return blockstate.getBlock() == this;
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return mirrorIn == Mirror.NONE ? state : state.rotate(mirrorIn.toRotation(state.get(FACING))).func_235896_a_(HINGE);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, SIDE, HINGE);
    }

    public enum DoorSide implements IStringSerializable {
        INSIDE("inside"), OUTSIDE("outside");

        private final String name;

        DoorSide(String name) {
            this.name = name;
        }

        @Override
        public String getString() {
            return name;
        }

        public static DoorSide getSideForDirection(Direction blockFacing, Direction dir) {
            return blockFacing.getOpposite() == dir ? DoorSide.OUTSIDE : DoorSide.INSIDE;
        }
    }
}
