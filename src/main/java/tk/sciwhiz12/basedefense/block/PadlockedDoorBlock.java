package tk.sciwhiz12.basedefense.block;

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
import tk.sciwhiz12.basedefense.api.capablities.ICodeHolder;
import tk.sciwhiz12.basedefense.item.BrokenLockPiecesItem;
import tk.sciwhiz12.basedefense.item.IColorable;
import tk.sciwhiz12.basedefense.item.lock.AbstractPadlockItem;
import tk.sciwhiz12.basedefense.tileentity.LockableTile;
import tk.sciwhiz12.basedefense.tileentity.PadlockedDoorTile;
import tk.sciwhiz12.basedefense.util.UnlockHelper;
import tk.sciwhiz12.basedefense.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.util.text.TextFormatting.*;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.KEY;
import static tk.sciwhiz12.basedefense.Reference.Sounds;

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
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

    public final Block baseBlock;

    public PadlockedDoorBlock() {
        this(Blocks.IRON_DOOR);
    }

    public PadlockedDoorBlock(Block blockIn) {
        super(Block.Properties.copy(blockIn));
        this.baseBlock = blockIn;
        REPLACEMENT_MAP.put(blockIn.delegate, this.delegate);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SIDE, DoorSide.OUTSIDE)
                .setValue(HINGE, DoorHingeSide.LEFT).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getBlock() == this && state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PadlockedDoorTile();
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
            BlockRayTraceResult rayTrace) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.relative(Direction.DOWN);
            state = worldIn.getBlockState(pos);
        }
        if (worldIn.isLoaded(pos) && state.getBlock() == this) {
            ItemStack keyStack = player.getItemInHand(handIn);
            TileEntity te = worldIn.getBlockEntity(pos);
            if (te instanceof PadlockedDoorTile) {
                PadlockedDoorTile doorTile = (PadlockedDoorTile) te;
                if (!keyStack.isEmpty() && keyStack.getCapability(KEY).isPresent()) {
                    if (allowOpen(state.getValue(SIDE), state.getValue(FACING), rayTrace.getDirection())) {
                        IWorldPosCallable worldPos = Util.getOrDummy(worldIn, pos);
                        if (UnlockHelper.checkRemove(keyStack, doorTile, worldPos, player, true)) {
                            replaceDoor(worldIn, pos);
                        }
                        player.swing(handIn);
                        return ActionResultType.SUCCESS;
                    }
                } else if (handIn == Hand.OFF_HAND) {
                    if (player.isShiftKeyDown() && allowOpen(state.getValue(SIDE), state.getValue(FACING), rayTrace.getDirection())) {
                        ItemStack lockStack = doorTile.getLockStack();
                        if (!lockStack.isEmpty() && lockStack.hasCustomHoverName()) {
                            player.displayClientMessage(new TranslationTextComponent("status.basedefense.door.info",
                                            lockStack.getHoverName().plainCopy().withStyle(WHITE)).withStyle(YELLOW,
                                    ITALIC),
                                    true);
                        }
                    } else {
                        player.displayClientMessage(new TranslationTextComponent("status.basedefense.door.locked",
                                new TranslationTextComponent(this.baseBlock.getDescriptionId()).withStyle(WHITE))
                                .withStyle(GRAY, ITALIC), true);
                    }
                    worldIn.playSound(player, pos, Sounds.LOCKED_DOOR_ATTEMPT, SoundCategory.BLOCKS, 1.0F,
                            worldIn.random.nextFloat() * 0.1F + 0.9F);
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
        final BlockPos offPos = state.getValue(PadlockedDoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        final BlockState offState = worldIn.getBlockState(offPos);

        final Direction facing = state.getValue(FACING);
        final DoorHingeSide hinge = state.getValue(HINGE);
        final BlockState defState = this.baseBlock.defaultBlockState().setValue(DoorBlock.HINGE, hinge)
                .setValue(DoorBlock.FACING, facing).setValue(DoorBlock.OPEN, false);

        final BlockState newState = defState.setValue(DoorBlock.HALF, state.getValue(HALF));
        final BlockState newOffState = defState.setValue(DoorBlock.HALF, offState.getValue(HALF));

        int flags = Constants.BlockFlags.DEFAULT_AND_RERENDER | Constants.BlockFlags.NO_NEIGHBOR_DROPS;
        worldIn.setBlock(offPos, newOffState, flags);
        worldIn.setBlock(pos, newState, flags);
    }

    public boolean isValidLock(ItemStack stack) {
        return stack.getItem() instanceof AbstractPadlockItem;
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        TileEntity te = builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
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
                            ((BrokenLockPiecesItem) stack.getItem()).setPreviousName(stack, lockStack.getHoverName());
                        } else if (lockStack.hasCustomHoverName()) { stack.setHoverName(lockStack.getHoverName()); }
                    }
                    break;
                }
            }
        }
        return drops;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)) {
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
        return this.hasCollision ? state.getShape(worldIn, pos) : VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.getShape(worldIn, pos);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te,
            ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, Blocks.AIR.defaultBlockState(), te, stack);
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
        BlockState otherState = worldIn.getBlockState(otherPos);
        if (otherState.getValue(HALF) != half) {
            ItemStack itemstack = player.getMainHandItem();
            if (!worldIn.isClientSide && !player.isCreative() && player.hasCorrectToolForDrops(otherState)) {
                BlockPos tePos = (half == DoubleBlockHalf.LOWER) ? pos : otherPos;
                Block.dropResources(state, worldIn, pos, worldIn.getBlockEntity(tePos), player, itemstack);
                Block.dropResources(otherState, worldIn, otherPos, worldIn.getBlockEntity(tePos), player, itemstack);
            }
            worldIn.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
            worldIn.levelEvent(player, Constants.WorldEvents.BREAK_BLOCK_EFFECTS, otherPos, Block.getId(otherState));
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP);
        } else {
            return blockstate.getBlock() == this;
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return mirrorIn == Mirror.NONE ? state : state.rotate(mirrorIn.getRotation(state.getValue(FACING))).cycle(HINGE);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, SIDE, HINGE);
    }

    public enum DoorSide implements IStringSerializable {
        INSIDE("inside"), OUTSIDE("outside");

        private final String name;

        DoorSide(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public static DoorSide getSideForDirection(Direction blockFacing, Direction dir) {
            return blockFacing.getOpposite() == dir ? DoorSide.OUTSIDE : DoorSide.INSIDE;
        }
    }
}
