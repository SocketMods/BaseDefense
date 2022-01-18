package tk.sciwhiz12.basedefense.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.IRegistryDelegate;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.Reference.Sounds;
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

import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.ITALIC;
import static net.minecraft.ChatFormatting.WHITE;
import static net.minecraft.ChatFormatting.YELLOW;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.KEY;

public class PadlockedDoorBlock extends Block implements EntityBlock {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new PadlockedDoorTile(pos, state) : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult rayTrace) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.relative(Direction.DOWN);
            state = worldIn.getBlockState(pos);
        }
        if (worldIn.isLoaded(pos) && state.getBlock() == this) {
            ItemStack keyStack = player.getItemInHand(handIn);
            @Nullable BlockEntity te = worldIn.getBlockEntity(pos);
            if (te instanceof PadlockedDoorTile) {
                PadlockedDoorTile doorTile = (PadlockedDoorTile) te;
                if (!keyStack.isEmpty() && keyStack.getCapability(KEY).isPresent()) {
                    if (allowOpen(state.getValue(SIDE), state.getValue(FACING), rayTrace.getDirection())) {
                        ContainerLevelAccess worldPos = Util.getOrDummy(worldIn, pos);
                        if (UnlockHelper.checkRemove(keyStack, doorTile, worldPos, player, true)) {
                            replaceDoor(worldIn, pos);
                        }
                        player.swing(handIn);
                        return InteractionResult.SUCCESS;
                    }
                } else if (handIn == InteractionHand.OFF_HAND) {
                    if (player.isShiftKeyDown() && allowOpen(state.getValue(SIDE), state.getValue(FACING), rayTrace.getDirection())) {
                        ItemStack lockStack = doorTile.getLockStack();
                        if (!lockStack.isEmpty() && lockStack.hasCustomHoverName()) {
                            player.displayClientMessage(new TranslatableComponent("status.basedefense.door.info",
                                    lockStack.getHoverName().plainCopy().withStyle(WHITE)).withStyle(YELLOW,
                                    ITALIC),
                                true);
                        }
                    } else {
                        player.displayClientMessage(new TranslatableComponent("status.basedefense.door.locked",
                            new TranslatableComponent(this.baseBlock.getDescriptionId()).withStyle(WHITE))
                            .withStyle(GRAY, ITALIC), true);
                    }
                    worldIn.playSound(player, pos, Sounds.LOCKED_DOOR_ATTEMPT, SoundSource.BLOCKS, 1.0F,
                        worldIn.random.nextFloat() * 0.1F + 0.9F);
                }
            }
        }
        return InteractionResult.PASS;
    }

    private boolean allowOpen(DoorSide lockSide, Direction blockFacing, Direction interactFacing) {
        return lockSide == DoorSide.getSideForDirection(blockFacing, interactFacing);
    }

    private void replaceDoor(Level worldIn, BlockPos pos) {
        final BlockState state = worldIn.getBlockState(pos);
        final BlockPos offPos = state.getValue(PadlockedDoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        final BlockState offState = worldIn.getBlockState(offPos);

        final Direction facing = state.getValue(FACING);
        final DoorHingeSide hinge = state.getValue(HINGE);
        final BlockState defState = this.baseBlock.defaultBlockState().setValue(DoorBlock.HINGE, hinge)
            .setValue(DoorBlock.FACING, facing).setValue(DoorBlock.OPEN, false);

        final BlockState newState = defState.setValue(DoorBlock.HALF, state.getValue(HALF));
        final BlockState newOffState = defState.setValue(DoorBlock.HALF, offState.getValue(HALF));

        int flags = UPDATE_ALL_IMMEDIATE | UPDATE_SUPPRESS_DROPS;
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
        @Nullable BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
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
                        } else if (lockStack.hasCustomHoverName()) {
                            stack.setHoverName(lockStack.getHoverName());
                        }
                    }
                    break;
                }
            }
        }
        return drops;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
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

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.hasCollision ? state.getShape(worldIn, pos) : Shapes.empty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return state.getShape(worldIn, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, Blocks.AIR.defaultBlockState(), te, stack);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
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
            worldIn.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, otherPos, Block.getId(otherState));
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP);
        } else {
            return blockstate.getBlock() == this;
        }
    }

    @SuppressWarnings("deprecation")
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, SIDE, HINGE);
    }

    public enum DoorSide implements StringRepresentable {
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
