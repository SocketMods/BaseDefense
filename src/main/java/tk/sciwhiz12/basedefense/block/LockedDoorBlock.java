package tk.sciwhiz12.basedefense.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import tk.sciwhiz12.basedefense.Reference.Sounds;
import tk.sciwhiz12.basedefense.item.LockedBlockItem;
import tk.sciwhiz12.basedefense.tileentity.LockedDoorTile;
import tk.sciwhiz12.basedefense.util.UnlockHelper;

import javax.annotation.Nullable;

import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.ITALIC;
import static net.minecraft.ChatFormatting.WHITE;
import static net.minecraft.ChatFormatting.YELLOW;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;

public class LockedDoorBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
    public final Block baseBlock;

    public LockedDoorBlock() {
        this(Blocks.IRON_DOOR);
    }

    public LockedDoorBlock(Block block) {
        super(Block.Properties.copy(block));
        this.baseBlock = block;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HINGE, DoorHingeSide.LEFT)
            .setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false).setValue(LOCKED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new LockedDoorTile(pos, state) : null;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult rayTrace) {
        if (worldIn.isLoaded(pos) && state.getBlock() == this) { // verify that block is loaded
            DoubleBlockHalf half = state.getValue(HALF);
            BlockPos otherPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            if (!worldIn.isLoaded(otherPos) || worldIn.getBlockState(otherPos).getBlock() != this) {
                return InteractionResult.FAIL;
            }
            BlockPos lowerPos = half == DoubleBlockHalf.LOWER ? pos : pos.below();
            LockedDoorTile te = (LockedDoorTile) worldIn.getBlockEntity(lowerPos);
            if (te == null) {
                return InteractionResult.FAIL;
            }
            ItemStack heldStack = player.getItemInHand(handIn);
            if (state.getValue(LOCKED)) { // LOCKED
                if (!heldStack.isEmpty() && UnlockHelper.checkUnlock(heldStack, te, worldIn, lowerPos, player, true)) {
                    // LOCKED, KEY
                    BlockState newState;
                    if (player.isShiftKeyDown()) { // LOCKED, KEY, SNEAKING => toggle locked state
                        final boolean newLocked = !state.getValue(LOCKED);
                        newState = state.setValue(LOCKED, newLocked);
                        final SoundEvent sound = newLocked ? Sounds.LOCKED_DOOR_RELOCK : Sounds.LOCKED_DOOR_UNLOCK;
                        playSound(player, worldIn, pos, sound);
                    } else { // LOCKED, KEY, NOT SNEAKING => toggle open state
                        final boolean newOpen = !state.getValue(OPEN);
                        newState = state.setValue(OPEN, newOpen);
                        this.playDoorSound(player, worldIn, pos, newOpen);
                    }
                    setAndNotify(newState, pos, worldIn);
                    return InteractionResult.SUCCESS;
                } else { // LOCKED, NO KEY
                    ItemStack lock = te.getLockStack();
                    if (handIn == InteractionHand.OFF_HAND) {
                        if (player.isShiftKeyDown() && lock.hasCustomHoverName()) {
                            // LOCKED, NO KEY, SNEAKING => inform player of lock name
                            player.displayClientMessage(new TranslatableComponent("status.basedefense.door.info",
                                lock.getHoverName().plainCopy().withStyle(WHITE)).withStyle(YELLOW, ITALIC), true);
                        } else { // LOCKED, NO KEY, NOT SNEAKING => inform player that door is locked
                            player.displayClientMessage(new TranslatableComponent("status.basedefense.door.locked",
                                new TranslatableComponent(this.baseBlock.getDescriptionId()).withStyle(WHITE))
                                .withStyle(GRAY, ITALIC), true);
                        }
                        playSound(player, worldIn, pos, Sounds.LOCKED_DOOR_ATTEMPT);
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                }
            } else { // UNLOCKED
                if (player.isShiftKeyDown()) { // UNLOCKED, SNEAKING
                    if (!te.getLockStack().isEmpty()) {
                        // UNLOCKED, SNEAKING, HAS LOCK => set to locked and unopened state
                        boolean wasOpen = state.getValue(OPEN);
                        setAndNotify(state.setValue(LOCKED, true).setValue(OPEN, false), pos, worldIn);
                        playSound(player, worldIn, pos, Sounds.LOCKED_DOOR_RELOCK);
                        if (wasOpen) {
                            this.playDoorSound(player, worldIn, pos, false);
                        }
                    } else { // UNLOCKED, SNEAKING, NO LOCK
                        if (!heldStack.isEmpty() && heldStack.getCapability(LOCK).isPresent()) {
                            // UNLOCKED, SNEAKING, NO LOCK, HOLDING LOCK => set held lock to current lock,
                            // remove from inv, set to locked state
                            te.setLockStack(heldStack.copy());
                            heldStack.setCount(heldStack.getCount() - 1);
                            setAndNotify(state.setValue(LOCKED, true), pos, worldIn);
                            playSound(player, worldIn, pos, Sounds.LOCKED_DOOR_RELOCK);
                            te.requestModelDataUpdate();
                        } // UNLOCKED, SNEAKING, NO LOCK, NOT HOLDING LOCK => nothing;
                    }

                } else { // UNLOCKED, NOT SNEAKING => toggle open state
                    final boolean newOpen = !state.getValue(OPEN);
                    setAndNotify(state.setValue(OPEN, newOpen), pos, worldIn);
                    this.playDoorSound(player, worldIn, pos, newOpen);
                }

            }
        }
        return InteractionResult.SUCCESS;
    }

    private void setAndNotify(BlockState state, BlockPos pos, Level worldIn) {
        worldIn.setBlock(pos, state, UPDATE_ALL_IMMEDIATE);
        worldIn.neighborChanged(getOtherHalf(state, pos), this, pos);
    }

    private void playSound(Player player, Level world, BlockPos pos, SoundEvent event) {
        world.playSound(player, pos, event, SoundSource.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block fromBlockIn, BlockPos fromPos,
                                boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, fromBlockIn, fromPos, isMoving);
        if (fromBlockIn != this) return;
        BlockState otherState = worldIn.getBlockState(fromPos);
        if (otherState.getBlock() != this) return;
        worldIn.setBlockAndUpdate(pos, state.setValue(LOCKED, otherState.getValue(LOCKED)).setValue(OPEN, otherState.getValue(OPEN)));
    }

    private BlockPos getOtherHalf(BlockState selfState, BlockPos selfPos) {
        return selfState.getValue(HALF) == DoubleBlockHalf.LOWER ? selfPos.above() : selfPos.below();
    }

    private void playDoorSound(Player player, Level worldIn, BlockPos pos, boolean isOpening) {
        worldIn.levelEvent(player, isOpening ? this.getOpenSound() : this.getCloseSound(), pos, 0);
    }

    private int getCloseSound() {
        return this.material == Material.METAL ? LevelEvent.SOUND_CLOSE_IRON_DOOR : LevelEvent.SOUND_CLOSE_WOODEN_DOOR;
    }

    private int getOpenSound() {
        return this.material == Material.METAL ? LevelEvent.SOUND_OPEN_IRON_DOOR : LevelEvent.SOUND_OPEN_WOODEN_DOOR;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        boolean closed = !state.getValue(OPEN);
        boolean rightHinge = state.getValue(HINGE) == DoorHingeSide.RIGHT;
        switch (direction) {
            case EAST:
            default:
                return closed ? EAST_AABB : (rightHinge ? NORTH_AABB : SOUTH_AABB);
            case SOUTH:
                return closed ? SOUTH_AABB : (rightHinge ? EAST_AABB : WEST_AABB);
            case WEST:
                return closed ? WEST_AABB : (rightHinge ? SOUTH_AABB : NORTH_AABB);
            case NORTH:
                return closed ? NORTH_AABB : (rightHinge ? WEST_AABB : EAST_AABB);
        }
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        boolean locked = false;
        if (!stack.isEmpty() && stack.getItem() instanceof LockedBlockItem) {
            BlockEntity te = worldIn.getBlockEntity(pos);
            if (te instanceof LockedDoorTile) {
                ((LockedDoorTile) te).setLockStack(((LockedBlockItem) stack.getItem()).getLockStack(stack));
                locked = true;
                worldIn.setBlock(pos, state.setValue(LOCKED, locked), UPDATE_ALL_IMMEDIATE);
            }
        }
        worldIn.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(LOCKED, locked), UPDATE_ALL_IMMEDIATE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
                                  BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = stateIn.getValue(HALF);
        if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
            if (facingState.getBlock() == this && facingState.getValue(HALF) != doubleblockhalf) {
                return stateIn.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN))
                    .setValue(HINGE, facingState.getValue(HINGE)).setValue(LOCKED, facingState.getValue(LOCKED));
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        } else {
            if (doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn
                .canSurvive(worldIn, currentPos)) {
                return Blocks.AIR.defaultBlockState();
            } else {
                return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        if (blockpos.getY() < 255 && context.getLevel().getBlockState(blockpos.above()).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection())
                .setValue(HINGE, this.getHingeSide(context)).setValue(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    private DoorHingeSide getHingeSide(BlockPlaceContext context) {
        BlockGetter world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Direction horizFacing = context.getHorizontalDirection();
        BlockPos abovePos = blockPos.above();
        Direction leftFacing = horizFacing.getCounterClockWise();
        BlockPos leftPos = blockPos.relative(leftFacing);
        BlockState leftState = world.getBlockState(leftPos);
        BlockPos leftAbovePos = abovePos.relative(leftFacing);
        BlockState leftAboveState = world.getBlockState(leftAbovePos);
        Direction rightFacing = horizFacing.getClockWise();
        BlockPos rightPos = blockPos.relative(rightFacing);
        BlockState rightState = world.getBlockState(rightPos);
        BlockPos rightAbovePos = abovePos.relative(rightFacing);
        BlockState rightAboveState = world.getBlockState(rightAbovePos);
        int i = (leftState.isCollisionShapeFullBlock(world, leftPos) ? -1 : 0) + (leftAboveState
            .isCollisionShapeFullBlock(world, leftAbovePos) ? -1 : 0) + (rightState
            .isCollisionShapeFullBlock(world, rightPos) ? 1 : 0) + (rightAboveState
            .isCollisionShapeFullBlock(world, rightAbovePos) ? 1 : 0);
        boolean leftHasDoor = leftState.is(this) && leftState.getValue(HALF) == DoubleBlockHalf.LOWER;
        boolean rightHasDoor = rightState.is(this) && rightState.getValue(HALF) == DoubleBlockHalf.LOWER;
        if ((!leftHasDoor || rightHasDoor) && i <= 0) {
            if ((!rightHasDoor || leftHasDoor) && i >= 0) {
                int xOffset = horizFacing.getStepX();
                int yOffset = horizFacing.getStepZ();
                Vec3 hitVec = context.getClickLocation();
                double d0 = hitVec.x - (double) blockPos.getX();
                double d1 = hitVec.z - (double) blockPos.getZ();
                return (xOffset >= 0 || !(d1 < 0.5D)) && (xOffset <= 0 || !(d1 > 0.5D)) && (yOffset >= 0 || !(d0 > 0.5D)) && (yOffset <= 0 || !(d0 < 0.5D)) ?
                    DoorHingeSide.LEFT :
                    DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        if (!worldIn.isClientSide && player.isCreative()) {
            // Coped from DoublePlantBlock.removeBottomHalf(worldIn, pos, state, player)
            if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                BlockPos lowerPos = pos.below();
                BlockState lowerState = worldIn.getBlockState(lowerPos);
                if (lowerState.getBlock() == state.getBlock() && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    worldIn.setBlock(lowerPos, Blocks.AIR.defaultBlockState(),
                        UPDATE_SUPPRESS_DROPS | UPDATE_ALL);
                    worldIn.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, lowerPos, Block.getId(lowerState));
                }
            }
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        switch (type) {
            case AIR:
            case LAND:
                return state.getValue(OPEN);
            case WATER:
            default:
                return false;
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockPos downPos = pos.below();
        BlockState downState = worldIn.getBlockState(downPos);
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return downState.isFaceSturdy(worldIn, downPos, Direction.UP);
        } else {
            return downState.getBlock() == this;
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, HINGE, OPEN, LOCKED);
    }
}
