package sciwhiz12.basedefense.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import sciwhiz12.basedefense.item.lock.LockCoreItem;
import sciwhiz12.basedefense.tileentity.LockableDoorTile;

public class LockableDoorBlock extends LockableBaseBlock {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(
        0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D
    );
    protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(
        0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D
    );
    protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(
        13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D
    );
    protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(
        0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D
    );

    public LockableDoorBlock() {
        super(
            Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F).sound(
                SoundType.METAL
            ).notSolid()
        );
        this.setDefaultState(
            this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(
                OPEN, Boolean.valueOf(false)
            ).with(HINGE, DoorHingeSide.LEFT).with(HALF, DoubleBlockHalf.LOWER)
        );
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new LockableDoorTile();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos,
            PlayerEntity player, Hand handIn, BlockRayTraceResult rayTrace) {
        if (!worldIn.isRemote && state.get(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.offset(Direction.DOWN);
            state = worldIn.getBlockState(pos);
        }
        ActionResultType result = super.onBlockActivated(
            state, worldIn, pos, player, handIn, rayTrace
        );
        if (result.isSuccess()) {
            state = state.cycle(OPEN);
            worldIn.setBlockState(pos, state, 10);
            this.playSound(null, worldIn, pos, state.get(OPEN));
        }
        return result;
    }

    @Override
    public boolean isValidLock(ItemStack stack) {
        return stack.getItem() instanceof LockCoreItem;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos,
            ISelectionContext context) {
        Direction direction = state.get(FACING);
        boolean open = !state.get(OPEN);
        boolean rightHinge = state.get(HINGE) == DoorHingeSide.RIGHT;
        switch (direction) {
            case EAST:
            default:
                return open ? EAST_AABB : (rightHinge ? NORTH_AABB : SOUTH_AABB);
            case SOUTH:
                return open ? SOUTH_AABB : (rightHinge ? EAST_AABB : WEST_AABB);
            case WEST:
                return open ? WEST_AABB : (rightHinge ? SOUTH_AABB : NORTH_AABB);
            case NORTH:
                return open ? NORTH_AABB : (rightHinge ? WEST_AABB : EAST_AABB);
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing,
            BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
        if (facing.getAxis() == Direction.Axis.Y
                && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
            return facingState.getBlock() == this && facingState.get(HALF) != doubleblockhalf
                    ? stateIn.with(FACING, facingState.get(FACING)).with(
                        OPEN, facingState.get(OPEN)
                    ).with(HINGE, facingState.get(HINGE)) : Blocks.AIR.getDefaultState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn
                .isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState()
                        : super.updatePostPlacement(
                            stateIn, facing, facingState, worldIn, currentPos, facingPos
                        );
        }
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state,
            @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state,
            PlayerEntity player) {
        DoubleBlockHalf doubleblockhalf = state.get(HALF);
        BlockPos blockpos = doubleblockhalf == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (blockstate.getBlock() == this && blockstate.get(HALF) != doubleblockhalf) {
            worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
            worldIn.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
            ItemStack itemstack = player.getHeldItemMainhand();
            if (!worldIn.isRemote && !player.isCreative() && player.canHarvestBlock(blockstate)) {
                Block.spawnDrops(state, worldIn, pos, (TileEntity) null, player, itemstack);
                Block.spawnDrops(
                    blockstate, worldIn, blockpos, (TileEntity) null, player, itemstack
                );
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos,
            PathType type) {
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
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        if (blockpos.getY() < (context.getWorld().getHeight() - 1) && context.getWorld()
            .getBlockState(blockpos.up()).isReplaceable(context)) {
            return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(
                HINGE, this.getHingeSide(context)
            ).with(OPEN, Boolean.FALSE).with(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer,
            ItemStack stack) {
        worldIn.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
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
        int i = (blockstate.isCollisionShapeOpaque(iblockreader, blockpos2) ? -1 : 0) + (blockstate1
            .isCollisionShapeOpaque(iblockreader, blockpos3) ? -1 : 0) + (blockstate2
                .isCollisionShapeOpaque(iblockreader, blockpos4) ? 1 : 0) + (blockstate3
                    .isCollisionShapeOpaque(iblockreader, blockpos5) ? 1 : 0);
        boolean flag = blockstate.getBlock() == this && blockstate.get(
            HALF
        ) == DoubleBlockHalf.LOWER;
        boolean flag1 = blockstate2.getBlock() == this && blockstate2.get(
            HALF
        ) == DoubleBlockHalf.LOWER;
        if ((!flag || flag1) && i <= 0) {
            if ((!flag1 || flag) && i >= 0) {
                int j = direction.getXOffset();
                int k = direction.getZOffset();
                Vec3d vec3d = p_208073_1_.getHitVec();
                double d0 = vec3d.x - (double) blockpos.getX();
                double d1 = vec3d.z - (double) blockpos.getZ();
                return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0
                        || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT
                                : DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }

    public void toggleDoor(World worldIn, BlockPos pos, boolean open) {
        BlockState blockstate = worldIn.getBlockState(pos);
        if (blockstate.getBlock() == this && blockstate.get(OPEN) != open) {
            worldIn.setBlockState(pos, blockstate.with(OPEN, Boolean.valueOf(open)), 10);
            this.playSound(null, worldIn, pos, open);
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

    private void playSound(PlayerEntity player, World worldIn, BlockPos pos, boolean isOpening) {
        worldIn.playEvent(
            player, isOpening ? Constants.WorldEvents.IRON_DOOR_OPEN_SOUND
                    : Constants.WorldEvents.IRON_DOOR_CLOSE_SOUND, pos, 0
        );
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return mirrorIn == Mirror.NONE ? state
                : state.rotate(mirrorIn.toRotation(state.get(FACING))).cycle(HINGE);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, OPEN, HINGE);
    }
}
