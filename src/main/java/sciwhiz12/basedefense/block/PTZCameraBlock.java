package sciwhiz12.basedefense.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import sciwhiz12.basedefense.tileentity.PTZCameraTile;

import javax.annotation.Nullable;

import static sciwhiz12.basedefense.util.ShapeUtil.rotateCuboidShape;

public class PTZCameraBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SOUTH_SHAPE = makeCuboidShape(4.5D, 7.5D, 0D, 11.5D, 13.5D, 6.5D);
    private static final VoxelShape EAST_SHAPE = rotateCuboidShape(SOUTH_SHAPE, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape NORTH_SHAPE = rotateCuboidShape(SOUTH_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE = rotateCuboidShape(SOUTH_SHAPE, Rotation.CLOCKWISE_90);

    public PTZCameraBlock() {
        super(Block.Properties.create(Material.IRON).notSolid());
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.SOUTH));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PTZCameraTile();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            default:
            case SOUTH:
                return SOUTH_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case NORTH:
                return NORTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
        }
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        Direction facing = state.get(FACING);
        BlockPos blockPos = pos.offset(facing.getOpposite());
        BlockState blockState = worldIn.getBlockState(blockPos);
        return facing.getAxis().isHorizontal() && blockState.isSolidSide(worldIn, blockPos, facing);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
            BlockPos currentPos, BlockPos facingPos) {
        return facing.getOpposite() == stateIn.get(FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR
                .getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = this.getDefaultState();
        IWorldReader world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction[] lookDir = context.getNearestLookingDirections();

        for (Direction direction : lookDir) {
            if (direction.getAxis().isHorizontal()) {
                Direction opp = direction.getOpposite();
                state = state.with(FACING, opp);
                if (state.isValidPosition(world, pos)) { return state; }
            }
        }

        return null;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos,
            boolean isMoving) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof PTZCameraTile) { ((PTZCameraTile) te).calculateMaxYaw(); }
    }
}
