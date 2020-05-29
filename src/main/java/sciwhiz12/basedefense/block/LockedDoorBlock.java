package sciwhiz12.basedefense.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.color.IBlockColor;
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
import sciwhiz12.basedefense.api.lock.Decision;
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.ILock;
import sciwhiz12.basedefense.api.lock.ILockable;
import sciwhiz12.basedefense.item.lock.LockCoreItem;
import sciwhiz12.basedefense.tileentity.LockedDoorTile;

import static net.minecraftforge.common.util.Constants.BlockFlags.DEFAULT_AND_RERENDER;

public class LockedDoorBlock extends LockableBaseBlock {
    public static final IBlockColor COLOR = (state, world, pos, tintIndex) -> {
        if (state.getBlock() instanceof LockedDoorBlock) {
            LockedDoorTile tile = (LockedDoorTile) world.getTileEntity(pos);
            if (tile != null && tile.hasColors()) {
                int[] colors = tile.getColors();
                // 0 : NONE, 1 : ind. 0 ; 2 : inds. 1, 2 ;
                if (colors.length - 1 >= tintIndex) { return colors[tintIndex]; }
            }
        }
        return -1;
    };
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

    public LockedDoorBlock(Block blockIn) {
        super(Block.Properties.from(blockIn));
        this.setDefaultState(
            this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HINGE, DoorHingeSide.LEFT).with(
                HALF, DoubleBlockHalf.LOWER
            ).with(OPEN, false).with(LOCKED, false)
        );
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
        if (!worldIn.isRemote && worldIn.isBlockPresent(pos) && state.getBlock() == this) { // verify that block is loaded
            if (state.get(LOCKED)) { // LOCKED
                if (checkUnlock(player.getHeldItem(handIn), worldIn, getLowerHalf(state, pos), player)) { // LOCKED, KEY
                    if (player.isSneaking()) { // LOCKED, KEY, SNEAKING => toggle locked state
                        worldIn.setBlockState(pos, state.with(LOCKED, !state.get(LOCKED)), DEFAULT_AND_RERENDER);
                        worldIn.neighborChanged(getOtherHalf(state, pos), this, pos);
                        return ActionResultType.SUCCESS; // END ACTION;
                    } else { // LOCKED, KEY, NOT SNEAKING => toggle open state
                        final boolean newOpen = !state.get(OPEN);
                        worldIn.setBlockState(pos, state.with(OPEN, newOpen), DEFAULT_AND_RERENDER);
                        this.playSound(worldIn, pos, newOpen);
                        worldIn.neighborChanged(getOtherHalf(state, pos), this, pos);
                        return ActionResultType.SUCCESS; // END ACTION;
                    }

                } else { // LOCKED, NO KEY
                    ItemStack lock = this.getLock(worldIn, getLowerHalf(state, pos));
                    if (player.isSneaking() && lock.hasDisplayName()) { // LOCKED, NO KEY, SNEAKING => inform player of lock
                                                                        // name
                        if (lock.hasDisplayName()) {
                            player.sendStatusMessage(
                                new TranslationTextComponent(
                                    "\"%s\"", lock.getDisplayName().applyTextStyle(TextFormatting.WHITE)
                                ).applyTextStyle(TextFormatting.YELLOW), true
                            );
                        }
                        return ActionResultType.PASS; // END ACTION;
                    } else { // LOCKED, NO KEY, NOT SNEAKING => inform player that door is locked
                        player.sendStatusMessage(
                            new TranslationTextComponent(
                                "Door is locked!", new TranslationTextComponent(this.getTranslationKey()).applyTextStyle(
                                    TextFormatting.WHITE
                                )
                            ).applyTextStyle(TextFormatting.GRAY), true
                        );
                        return ActionResultType.PASS; // END ACTION;
                    }

                }
            } else { // UNLOCKED
                if (player.isSneaking()) { // UNLOCKED, SNEAKING
                    if (this.hasLock(worldIn, getLowerHalf(state, pos))) {
                        // UNLOCKED, SNEAKING, HAS LOCK => set to locked and unopened state
                        worldIn.setBlockState(pos, state.with(LOCKED, true).with(OPEN, false), DEFAULT_AND_RERENDER);
                        worldIn.neighborChanged(getOtherHalf(state, pos), this, pos);
                        return ActionResultType.SUCCESS; // END ACTION;
                    } else { // UNLOCKED, SNEAKING, NO LOCK
                        ItemStack heldStack = player.getHeldItem(handIn);
                        if (!heldStack.isEmpty() && this.isValidLock(heldStack)) {
                            // UNLOCKED, SNEAKING, NO LOCK, HOLDING LOCK => set held lock to current lock,
                            // remove from inv, set to locked state
                            this.setLock(worldIn, getLowerHalf(state, pos), heldStack);
                            player.inventory.decrStackSize(player.inventory.getSlotFor(heldStack), 1);
                            worldIn.setBlockState(pos, state.with(LOCKED, true), DEFAULT_AND_RERENDER);
                            worldIn.neighborChanged(getOtherHalf(state, pos), this, pos);
                            return ActionResultType.SUCCESS; // END ACTION;
                        } // UNLOCKED, SNEAKING, NO LOCK, NOT HOLDING LOCK => nothing;

                    }

                } else { // UNLOCKED, NOT SNEAKING => toggle open state
                    final boolean newOpen = !state.get(OPEN);
                    worldIn.setBlockState(pos, state.with(OPEN, newOpen), DEFAULT_AND_RERENDER);
                    this.playSound(worldIn, pos, newOpen);
                    worldIn.neighborChanged(getOtherHalf(state, pos), this, pos);
                    return ActionResultType.SUCCESS; // END ACTION;
                }

            }
        }
        return ActionResultType.SUCCESS;
    }

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

    private BlockPos getLowerHalf(BlockState selfState, BlockPos selfPos) {
        return selfState.get(HALF) == DoubleBlockHalf.LOWER ? selfPos : selfPos.down();
    }

    private boolean checkUnlock(ItemStack keyStack, World world, BlockPos pos, PlayerEntity player) {
        if (this.hasLock(world, pos) && !keyStack.isEmpty() && keyStack.getItem() instanceof IKey) {
            IKey key = (IKey) keyStack.getItem();
            ItemStack lockStack = this.getLock(world, pos);
            if (lockStack.getItem() instanceof ILock) {
                ILock lock = (ILock) lockStack.getItem();
                boolean success = key.canUnlock(lockStack, keyStack, world, pos, this, player);
                success = success && lock.isUnlockAllowed(lockStack, keyStack, world, pos, this, player);
                success = success && this.isUnlockAllowed(lockStack, keyStack, world, pos, this, player);
                if (success) {
                    if (lock.onUnlock(lockStack, keyStack, world, pos, this, player) == Decision.CONTINUE) {
                        key.onUnlock(lockStack, keyStack, world, pos, this, player);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void playSound(World worldIn, BlockPos pos, boolean isOpening) {
        worldIn.playEvent((PlayerEntity) null, isOpening ? this.getOpenSound() : this.getCloseSound(), pos, 0);
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
    public boolean isValidLock(ItemStack stack) {
        return stack.getItem() instanceof LockCoreItem;
    }

    @Override
    public boolean isUnlockAllowed(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos, ILockable block,
            PlayerEntity player) {
        return true;
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
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te,
            ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        boolean locked = false;
        if (stack.hasTag() && stack.getChildTag("LockItem") != null) {
            this.setLock(worldIn, pos, ItemStack.read(stack.getChildTag("LockItem")));
            locked = true;
            worldIn.setBlockState(pos, state.with(LOCKED, locked), DEFAULT_AND_RERENDER);
        }
        worldIn.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER).with(LOCKED, locked), DEFAULT_AND_RERENDER);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
            BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
        if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
            if (facingState.getBlock() == this && facingState.get(HALF) != doubleblockhalf) {
                return stateIn.with(FACING, facingState.get(FACING)).with(OPEN, facingState.get(OPEN)).with(
                    HINGE, facingState.get(HINGE)
                ).with(LOCKED, facingState.get(LOCKED));
            } else {
                return Blocks.AIR.getDefaultState();
            }
        } else {
            if (doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.isValidPosition(
                worldIn, currentPos
            )) {
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
            return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(
                HINGE, this.getHingeSide(context)
            ).with(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    private DoorHingeSide getHingeSide(BlockItemUseContext p_208073_1_) {
        IBlockReader iblockreader = p_208073_1_.getWorld();
        BlockPos blockpos = p_208073_1_.getPos();
        Direction direction = p_208073_1_.getPlacementHorizontalFacing();
        BlockPos blockpos1 = blockpos.up();
        Direction direction1 = direction.rotateYCCW();
        BlockPos blockpos2 = blockpos.offset(direction1);
        BlockState blockstate = iblockreader.getBlockState(blockpos2);
        BlockPos blockpos3 = blockpos1.offset(direction1);
        BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
        Direction direction2 = direction.rotateY();
        BlockPos blockpos4 = blockpos.offset(direction2);
        BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
        BlockPos blockpos5 = blockpos1.offset(direction2);
        BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
        int i = (blockstate.isCollisionShapeOpaque(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.isCollisionShapeOpaque(
            iblockreader, blockpos3
        ) ? -1 : 0) + (blockstate2.isCollisionShapeOpaque(iblockreader, blockpos4) ? 1 : 0) + (blockstate3
            .isCollisionShapeOpaque(iblockreader, blockpos5) ? 1 : 0);
        boolean flag = blockstate.getBlock() == this && blockstate.get(HALF) == DoubleBlockHalf.LOWER;
        boolean flag1 = blockstate2.getBlock() == this && blockstate2.get(HALF) == DoubleBlockHalf.LOWER;
        if ((!flag || flag1) && i <= 0) {
            if ((!flag1 || flag) && i >= 0) {
                int j = direction.getXOffset();
                int k = direction.getZOffset();
                Vec3d vec3d = p_208073_1_.getHitVec();
                double d0 = vec3d.x - (double) blockpos.getX();
                double d1 = vec3d.z - (double) blockpos.getZ();
                return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0
                        || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf doubleblockhalf = state.get(HALF);
        BlockPos otherPos = doubleblockhalf == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        BlockState otherState = worldIn.getBlockState(otherPos);
        if (otherState.get(HALF) != doubleblockhalf) {
            ItemStack itemstack = player.getHeldItemMainhand();
            if (!worldIn.isRemote && !player.isCreative() && player.canHarvestBlock(otherState)) {
                BlockPos tePos = doubleblockhalf == DoubleBlockHalf.LOWER ? pos : otherPos;
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

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return blockstate.isSolidSide(worldIn, blockpos, Direction.UP);
        } else {
            return blockstate.getBlock() == this;
        }
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return mirrorIn == Mirror.NONE ? state : state.rotate(mirrorIn.toRotation(state.get(FACING))).cycle(HINGE);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, HINGE, OPEN, LOCKED);
    }
}
