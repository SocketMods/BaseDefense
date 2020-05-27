package sciwhiz12.basedefense.block;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.IRegistryDelegate;
import sciwhiz12.basedefense.api.lock.Decision;
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.ILock;
import sciwhiz12.basedefense.api.lock.ILockable;
import sciwhiz12.basedefense.item.lock.PadlockItem;
import sciwhiz12.basedefense.tileentity.PadlockedDoorTile;

public class PadlockedDoorBlock extends LockableBaseBlock {
    private static final Map<IRegistryDelegate<Block>, IRegistryDelegate<Block>> replacement_block_map = new HashMap<>();

    public static PadlockedDoorBlock getReplacement(Block blockIn) {
        return (PadlockedDoorBlock) replacement_block_map.get(blockIn.delegate).get();
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoorSide> SIDE = EnumProperty.create("padlock", DoorSide.class);
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

    private final Block block;

    public PadlockedDoorBlock() {
        this(Blocks.IRON_DOOR);
    }

    public PadlockedDoorBlock(Block blockIn) {
        super(Block.Properties.from(blockIn));
        this.block = blockIn;
        replacement_block_map.put(blockIn.delegate, this.delegate);
        this.setDefaultState(
            this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(SIDE, DoorSide.OUTSIDE).with(
                HINGE, DoorHingeSide.LEFT
            ).with(HALF, DoubleBlockHalf.LOWER)
        );
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
        ItemStack keyStack = player.getHeldItem(handIn);
        if (!worldIn.isRemote && worldIn.isBlockLoaded(pos) && state.getBlock() == this && allowOpen(
            state.get(SIDE), state.get(FACING), rayTrace.getFace()
        )) {
            if (this.hasLock(worldIn, pos) && !keyStack.isEmpty() && keyStack.getItem() instanceof IKey) {
                IKey key = (IKey) keyStack.getItem();
                ItemStack lockStack = this.getLock(worldIn, pos);
                ILock lock = (ILock) lockStack.getItem();
                boolean success = key.canUnlock(lockStack, keyStack, worldIn, pos, this, player);
                success = success && lock.isUnlockAllowed(lockStack, keyStack, worldIn, pos, this, player);
                // we skip checking #isUnlockAllowed for block because we (the block) allow it
                if (success) {
                    if (lock.onUnlock(lockStack, keyStack, worldIn, pos, this, player) == Decision.CONTINUE) {
                        key.onUnlock(lockStack, keyStack, worldIn, pos, this, player);
                        if (this.hasLock(worldIn, pos)) { dropLock(player, worldIn, pos, lockStack); }
                        replaceDoor(worldIn, pos);
                    }
                }
                return ActionResultType.SUCCESS;
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
        final BlockState defState = this.block.getDefaultState().with(DoorBlock.HINGE, hinge).with(DoorBlock.FACING, facing)
            .with(DoorBlock.OPEN, this.block.getMaterial(this.block.getDefaultState()) == Material.IRON ? false : true);

        final BlockState newState = defState.with(DoorBlock.HALF, state.get(HALF));
        final BlockState newOffState = defState.with(DoorBlock.HALF, offState.get(HALF));

        int flags = Constants.BlockFlags.DEFAULT_AND_RERENDER | Constants.BlockFlags.NO_NEIGHBOR_DROPS;
        worldIn.setBlockState(offPos, newOffState, flags);
        worldIn.setBlockState(pos, newState, flags);
    }

    private void dropLock(PlayerEntity player, World worldIn, BlockPos pos, ItemStack lockStack) {
        boolean flag = player.inventory.addItemStackToInventory(lockStack);
        if (flag && lockStack.isEmpty()) {
            lockStack.setCount(1);
            ItemEntity itementity1 = player.dropItem(lockStack, false);
            if (itementity1 != null) { itementity1.makeFakeItem(); }
            worldIn.playSound(
                (PlayerEntity) null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F)
                        * 2.0F
            );
            player.container.detectAndSendChanges();
        } else {
            ItemEntity itementity = player.dropItem(lockStack, false);
            if (itementity != null) {
                itementity.setNoPickupDelay();
                itementity.setOwnerId(player.getUniqueID());
            }
        }
        this.setLock(worldIn, pos, ItemStack.EMPTY);
    }

    @Override
    public boolean isValidLock(ItemStack stack) {
        return stack.getItem() instanceof PadlockItem;
    }

    @Override
    public boolean isUnlockAllowed(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos, ILockable block,
            PlayerEntity player) {
        return true;
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
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te,
            ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf doubleblockhalf = state.get(HALF);
        BlockPos otherPos = doubleblockhalf == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
        BlockState otherState = worldIn.getBlockState(otherPos);
        if (otherState.get(HALF) != doubleblockhalf) {
            worldIn.setBlockState(otherPos, Blocks.AIR.getDefaultState(), 35);
            worldIn.playEvent(player, Constants.WorldEvents.BREAK_BLOCK_EFFECTS, otherPos, Block.getStateId(otherState));
            ItemStack itemstack = player.getHeldItemMainhand();
            if (!worldIn.isRemote && !player.isCreative() && player.canHarvestBlock(otherState)) {
                Block.spawnDrops(state, worldIn, pos, worldIn.getTileEntity(pos), player, itemstack);
                Block.spawnDrops(otherState, worldIn, otherPos, worldIn.getTileEntity(otherPos), player, itemstack);
            }
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
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
        builder.add(HALF, FACING, SIDE, HINGE);
    }

    public static enum DoorSide implements IStringSerializable {
        INSIDE("inside"), OUTSIDE("outside");

        private final String name;

        private DoorSide(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        public static DoorSide getSideForDirection(Direction blockFacing, Direction dir) {
            return blockFacing.getOpposite() == dir ? DoorSide.OUTSIDE : DoorSide.INSIDE;
        }
    }
}
