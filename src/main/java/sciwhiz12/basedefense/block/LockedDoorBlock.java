package sciwhiz12.basedefense.block;

import static net.minecraftforge.common.util.Constants.BlockFlags.DEFAULT_AND_RERENDER;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import sciwhiz12.basedefense.init.ModCapabilities;
import sciwhiz12.basedefense.init.ModSounds;
import sciwhiz12.basedefense.tileentity.LockedDoorTile;
import sciwhiz12.basedefense.util.UnlockHelper;

public class LockedDoorBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

    public LockedDoorBlock() {
        this(Blocks.IRON_DOOR);
    }

    public LockedDoorBlock(Block block) {
        super(Block.Properties.from(block));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HINGE, DoorHingeSide.LEFT)
            .with(HALF, DoubleBlockHalf.LOWER).with(OPEN, false).with(LOCKED, false));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getBlock() == this && state.get(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new LockedDoorTile();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
            BlockRayTraceResult rayTrace) {
        if (worldIn.isBlockPresent(pos) && state.getBlock() == this) { // verify that block is loaded

            BlockPos otherPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
            if (!worldIn.isBlockPresent(otherPos) || worldIn.getBlockState(otherPos).getBlock() != this) {
                return ActionResultType.FAIL;
            }
            LockedDoorTile te = (LockedDoorTile) worldIn.getTileEntity(state.get(HALF) == DoubleBlockHalf.LOWER ? pos
                    : pos.down());
            if (te == null) { return ActionResultType.FAIL; }
            ItemStack heldStack = player.getHeldItem(handIn);
            if (state.get(LOCKED)) { // LOCKED
                if (UnlockHelper.checkUnlock(heldStack, te, worldIn, pos, player)) {
                    // LOCKED, KEY
                    BlockState newState = state;
                    if (player.isSneaking()) { // LOCKED, KEY, SNEAKING => toggle locked state
                        final boolean newLocked = !state.get(LOCKED);
                        newState = state.with(LOCKED, newLocked);
                        final SoundEvent sound = newLocked ? ModSounds.LOCKED_DOOR_RELOCK : ModSounds.LOCKED_DOOR_UNLOCK;
                        playSound(player, worldIn, pos, sound);
                    } else { // LOCKED, KEY, NOT SNEAKING => toggle open state
                        final boolean newOpen = !state.get(OPEN);
                        newState = state.with(OPEN, newOpen);
                        this.playDoorSound(player, worldIn, pos, newOpen);
                    }
                    setAndNotify(newState, pos, worldIn);
                    return ActionResultType.SUCCESS;
                } else { // LOCKED, NO KEY
                    ItemStack lock = te.getLockStack();
                    if (handIn == Hand.OFF_HAND) {
                        if (player.isSneaking() && lock.hasDisplayName()) {
                            // LOCKED, NO KEY, SNEAKING => inform player of lock name
                            player.sendStatusMessage(new TranslationTextComponent("\"%s\"", lock.getDisplayName()
                                .applyTextStyle(TextFormatting.WHITE)).applyTextStyle(TextFormatting.YELLOW), true);
                        } else { // LOCKED, NO KEY, NOT SNEAKING => inform player that door is locked
                            player.sendStatusMessage(new TranslationTextComponent("Door is locked!",
                                new TranslationTextComponent(this.getTranslationKey()).applyTextStyle(TextFormatting.WHITE))
                                    .applyTextStyle(TextFormatting.GRAY), true);
                        }
                        playSound(player, worldIn, pos, ModSounds.LOCKED_DOOR_ATTEMPT);
                        return ActionResultType.SUCCESS;
                    }
                    return ActionResultType.PASS;
                }
            } else { // UNLOCKED
                if (player.isSneaking()) { // UNLOCKED, SNEAKING
                    if (!te.getLockStack().isEmpty()) {
                        // UNLOCKED, SNEAKING, HAS LOCK => set to locked and unopened state
                        boolean wasOpen = state.get(OPEN);
                        setAndNotify(state.with(LOCKED, true).with(OPEN, false), pos, worldIn);
                        playSound(player, worldIn, pos, ModSounds.LOCKED_DOOR_RELOCK);
                        if (wasOpen) { this.playDoorSound(player, worldIn, pos, false); }
                    } else { // UNLOCKED, SNEAKING, NO LOCK
                        if (!heldStack.isEmpty() && heldStack.getCapability(ModCapabilities.LOCK).isPresent()) {
                            // UNLOCKED, SNEAKING, NO LOCK, HOLDING LOCK => set held lock to current lock,
                            // remove from inv, set to locked state
                            te.setLockStack(heldStack.copy());
                            heldStack.setCount(heldStack.getCount() - 1);
                            setAndNotify(state.with(LOCKED, true), pos, worldIn);
                            playSound(player, worldIn, pos, ModSounds.LOCKED_DOOR_RELOCK);
                            te.requestModelDataUpdate();
                        } // UNLOCKED, SNEAKING, NO LOCK, NOT HOLDING LOCK => nothing;
                    }

                } else { // UNLOCKED, NOT SNEAKING => toggle open state
                    final boolean newOpen = !state.get(OPEN);
                    setAndNotify(state.with(OPEN, newOpen), pos, worldIn);
                    this.playDoorSound(player, worldIn, pos, newOpen);
                }

            }
        }
        return ActionResultType.SUCCESS;
    }

    private void setAndNotify(BlockState state, BlockPos pos, World worldIn) {
        worldIn.setBlockState(pos, state, DEFAULT_AND_RERENDER);
        worldIn.neighborChanged(getOtherHalf(state, pos), this, pos);
    }

    private void playSound(PlayerEntity player, World world, BlockPos pos, SoundEvent event) {
        world.playSound(player, pos, event, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block fromBlockIn, BlockPos fromPos,
            boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, fromBlockIn, fromPos, isMoving);
        if (fromBlockIn != this) return;
        BlockState otherState = worldIn.getBlockState(fromPos);
        if (otherState.getBlock() != this) return;
        worldIn.setBlockState(pos, state.with(LOCKED, otherState.get(LOCKED)).with(OPEN, otherState.get(OPEN)));
    }

    private BlockPos getOtherHalf(BlockState selfState, BlockPos selfPos) {
        return selfState.get(HALF) == DoubleBlockHalf.LOWER ? selfPos.up() : selfPos.down();
    }

    private void playDoorSound(PlayerEntity player, World worldIn, BlockPos pos, boolean isOpening) {
        worldIn.playEvent(player, isOpening ? this.getOpenSound() : this.getCloseSound(), pos, 0);
    }

    private int getCloseSound() {
        return this.material == Material.IRON ? Constants.WorldEvents.IRON_DOOR_CLOSE_SOUND
                : Constants.WorldEvents.WOODEN_DOOR_CLOSE_SOUND;
    }

    private int getOpenSound() {
        return this.material == Material.IRON ? Constants.WorldEvents.IRON_DOOR_OPEN_SOUND
                : Constants.WorldEvents.WOODEN_DOOR_OPEN_SOUND;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction direction = state.get(FACING);
        boolean closed = !state.get(OPEN);
        boolean rightHinge = state.get(HINGE) == DoorHingeSide.RIGHT;
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
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te,
            ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        boolean locked = false;
        if (stack.hasTag() && stack.getChildTag("LockItem") != null) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te instanceof LockedDoorTile) {
                ((LockedDoorTile) te).setLockStack(ItemStack.read(stack.getChildTag("LockItem")));
                locked = true;
                worldIn.setBlockState(pos, state.with(LOCKED, locked), DEFAULT_AND_RERENDER);
            }
        }
        worldIn.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER).with(LOCKED, locked), DEFAULT_AND_RERENDER);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
            BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
        if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
            if (facingState.getBlock() == this && facingState.get(HALF) != doubleblockhalf) {
                return stateIn.with(FACING, facingState.get(FACING)).with(OPEN, facingState.get(OPEN)).with(HINGE,
                    facingState.get(HINGE)).with(LOCKED, facingState.get(LOCKED));
            } else {
                return Blocks.AIR.getDefaultState();
            }
        } else {
            if (doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.isValidPosition(worldIn,
                currentPos)) {
                return Blocks.AIR.getDefaultState();
            } else {
                return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        if (blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up()).isReplaceable(context)) {
            return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(HINGE, this.getHingeSide(
                context)).with(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    private DoorHingeSide getHingeSide(BlockItemUseContext context) {
        IBlockReader world = context.getWorld();
        BlockPos blockPos = context.getPos();
        Direction horizFacing = context.getPlacementHorizontalFacing();
        BlockPos abovePos = blockPos.up();
        Direction leftFacing = horizFacing.rotateYCCW();
        BlockPos leftPos = blockPos.offset(leftFacing);
        BlockState leftState = world.getBlockState(leftPos);
        BlockPos leftAbovePos = abovePos.offset(leftFacing);
        BlockState leftAboveState = world.getBlockState(leftAbovePos);
        Direction rightFacing = horizFacing.rotateY();
        BlockPos rightPos = blockPos.offset(rightFacing);
        BlockState rightState = world.getBlockState(rightPos);
        BlockPos rightAbovePos = abovePos.offset(rightFacing);
        BlockState rigthAboveState = world.getBlockState(rightAbovePos);
        int i = (leftState.isCollisionShapeOpaque(world, leftPos) ? -1 : 0) + (leftAboveState.isCollisionShapeOpaque(world,
            leftAbovePos) ? -1 : 0) + (rightState.isCollisionShapeOpaque(world, rightPos) ? 1 : 0) + (rigthAboveState
                .isCollisionShapeOpaque(world, rightAbovePos) ? 1 : 0);
        boolean leftHasDoor = leftState.getBlock() == this && leftState.get(HALF) == DoubleBlockHalf.LOWER;
        boolean rightHasDoor = rightState.getBlock() == this && rightState.get(HALF) == DoubleBlockHalf.LOWER;
        if ((!leftHasDoor || rightHasDoor) && i <= 0) {
            if ((!rightHasDoor || leftHasDoor) && i >= 0) {
                int xOffset = horizFacing.getXOffset();
                int yOffset = horizFacing.getZOffset();
                Vec3d hitVec = context.getHitVec();
                double d0 = hitVec.x - (double) blockPos.getX();
                double d1 = hitVec.z - (double) blockPos.getZ();
                return (xOffset >= 0 || !(d1 < 0.5D)) && (xOffset <= 0 || !(d1 > 0.5D)) && (yOffset >= 0 || !(d0 > 0.5D))
                        && (yOffset <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf half = state.get(HALF);
        BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.up() : pos.down();
        BlockState otherState = worldIn.getBlockState(otherPos);
        if (otherState.getBlock() == this && otherState.get(HALF) != half) {
            ItemStack heldItem = player.getHeldItemMainhand();
            if (!worldIn.isRemote && !player.isCreative() && player.canHarvestBlock(otherState)) {
                BlockPos tePos = (half == DoubleBlockHalf.LOWER) ? pos : otherPos;
                Block.spawnDrops(state, worldIn, pos, worldIn.getTileEntity(tePos), player, heldItem);
                Block.spawnDrops(otherState, worldIn, otherPos, worldIn.getTileEntity(tePos), player, heldItem);
            }
            worldIn.setBlockState(otherPos, Blocks.AIR.getDefaultState(), 35);
            worldIn.playEvent(player, Constants.WorldEvents.BREAK_BLOCK_EFFECTS, otherPos, Block.getStateId(otherState));
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        switch (type) {
            case LAND:
                return state.get(OPEN);
            case WATER:
                return false;
            case AIR:
                return state.get(OPEN);
            default:
                return false;
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos downPos = pos.down();
        BlockState downState = worldIn.getBlockState(downPos);
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return downState.isSolidSide(worldIn, downPos, Direction.UP);
        } else {
            return downState.getBlock() == this;
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return mirrorIn == Mirror.NONE ? state : state.rotate(mirrorIn.toRotation(state.get(FACING))).cycle(HINGE);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, HINGE, OPEN, LOCKED);
    }
}
