package sciwhiz12.basedefense.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import sciwhiz12.basedefense.UnlockHelper;
import sciwhiz12.basedefense.UnlockHelper.UnlockResult;
import sciwhiz12.basedefense.Util;
import sciwhiz12.basedefense.tileentity.LockableTile;

public abstract class LockableBaseBlock extends Block {
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

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand,
            BlockRayTraceResult rayTrace) {
        if (!worldIn.isRemote && worldIn.isBlockLoaded(pos)) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            ItemStack keyStack = player.getHeldItem(hand);
            IWorldPosCallable worldPos = Util.getOrDummy(worldIn, pos);
            UnlockResult result = UnlockHelper.checkUnlock(keyStack, tileEntity, worldPos, player);
            if (result.isSuccess()) {
                // TODO: add hook for disallowing unlock
                UnlockHelper.consumeIfPresent(keyStack, tileEntity, (key, lock) -> {
                    key.onUnlock(lock, worldPos, player);
                    lock.onUnlock(key, worldPos, player);
                });
                return ActionResultType.SUCCESS;
            }
            // if (!(origBlock instanceof ILockable)) { return ActionResultType.PASS; }
            // ILockable block = (ILockable) origBlock;
            //
            // if (block.hasLock(worldIn, pos)) {
            // ItemStack keyStack = player.getHeldItem(hand);
            // if (!keyStack.isEmpty() && keyStack.getItem() instanceof IKeyD) {
            // IKeyD keyD = (IKeyD) keyStack.getItem();
            // ItemStack lockStack = block.getLock(worldIn, pos);
            // ILockD lockD = (ILockD) lockStack.getItem();
            // boolean success = keyD.canUnlock(lockStack, keyStack, worldIn, pos, block,
            // player);
            // success = success && lockD.isUnlockAllowed(lockStack, keyStack, worldIn, pos,
            // block, player);
            // success = success && block.isUnlockAllowed(lockStack, keyStack, worldIn, pos,
            // block, player);
            // if (success) {
            // if (lockD.onUnlock(lockStack, keyStack, worldIn, pos, block, player) ==
            // Decision.CONTINUE) {
            // if (block.onUnlock(lockStack, keyStack, worldIn, pos, block, player) ==
            // Decision.CONTINUE) {
            // keyD.onUnlock(lockStack, keyStack, worldIn, pos, block, player);
            // }
            // }
            // return ActionResultType.SUCCESS;
            // }
            // return ActionResultType.FAIL;
            // }
            // }
            // ItemStack stack = player.getHeldItem(hand);
            // if (!block.hasLock(worldIn, pos) && !stack.isEmpty() &&
            // this.isValidLock(stack)) {
            // block.setLock(worldIn, pos, stack);
            // stack.setCount(stack.getCount() - 1);
            // return ActionResultType.CONSUME;
            // }
        }
        return ActionResultType.PASS;
    }
}
