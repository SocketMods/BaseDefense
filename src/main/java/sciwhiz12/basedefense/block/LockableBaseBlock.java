package sciwhiz12.basedefense.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import sciwhiz12.basedefense.api.lock.Decision;
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.ILock;
import sciwhiz12.basedefense.api.lock.ILockable;
import sciwhiz12.basedefense.tileentity.LockableTile;

public abstract class LockableBaseBlock extends Block implements ILockable {
    public LockableBaseBlock(Block.Properties builder) {
        super(builder);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new LockableTile();
    }

    public abstract boolean isValidLock(ItemStack stack);

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand,
            BlockRayTraceResult rayTrace) {
        if (!worldIn.isRemote && worldIn.isBlockLoaded(pos)) {

            Block origBlock = worldIn.getBlockState(pos).getBlock();
            if (!(origBlock instanceof ILockable)) { return ActionResultType.PASS; }
            ILockable block = (ILockable) origBlock;

            if (block.hasLock(worldIn, pos)) {
                ItemStack keyStack = player.getHeldItem(hand);
                if (!keyStack.isEmpty() && keyStack.getItem() instanceof IKey) {
                    IKey key = (IKey) keyStack.getItem();
                    ItemStack lockStack = block.getLock(worldIn, pos);
                    ILock lock = (ILock) lockStack.getItem();
                    boolean success = key.canUnlock(lockStack, keyStack, worldIn, pos, block, player);
                    success = success && lock.isUnlockAllowed(lockStack, keyStack, worldIn, pos, block, player);
                    success = success && block.isUnlockAllowed(lockStack, keyStack, worldIn, pos, block, player);
                    if (success) {
                        if (lock.onUnlock(lockStack, keyStack, worldIn, pos, block, player) == Decision.CONTINUE) {
                            if (block.onUnlock(lockStack, keyStack, worldIn, pos, block, player) == Decision.CONTINUE) {
                                key.onUnlock(lockStack, keyStack, worldIn, pos, block, player);
                            }
                        }
                        return ActionResultType.SUCCESS;
                    }
                    return ActionResultType.FAIL;
                }
            }
            ItemStack stack = player.getHeldItem(hand);
            if (!block.hasLock(worldIn, pos) && !stack.isEmpty() && this.isValidLock(stack)) {
                block.setLock(worldIn, pos, stack);
                stack.setCount(stack.getCount() - 1);
                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public ItemStack getLock(World world, BlockPos pos) {
        if (world.isBlockLoaded(pos) && world.getTileEntity(pos) != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof LockableTile) { return ((LockableTile) world.getTileEntity(pos)).getLock(); }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean hasLock(World world, BlockPos pos) {
        if (world.isBlockLoaded(pos) && world.getTileEntity(pos) != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof LockableTile) { return ((LockableTile) world.getTileEntity(pos)).hasLock(); }
        }
        return false;
    }

    @Override
    public void setLock(World world, BlockPos pos, ItemStack stack) {
        if (world.isBlockLoaded(pos) && world.getTileEntity(pos) != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof LockableTile) { ((LockableTile) world.getTileEntity(pos)).setLock(stack); }
        }
    }

    @Override
    public Decision onUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos, ILockable block,
            @Nullable PlayerEntity player) {
        return Decision.CONTINUE;
    }
}
