package sciwhiz12.basedefense.block;

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
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.ILock;
import sciwhiz12.basedefense.api.lock.LockContext;
import sciwhiz12.basedefense.tileentity.LockableTile;

public abstract class LockableBlock extends Block {
    public LockableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace) {
        if (!world.isRemote) {
            LockableTile te = (LockableTile) world.getTileEntity(pos);
            if (te.hasLock()) {
                ItemStack keyStack = player.getHeldItem(hand);
                if (!keyStack.isEmpty() && keyStack.getItem() instanceof IKey) {
                    IKey key = (IKey) keyStack.getItem();
                    LockContext ctx = new LockContext(
                            te.getLock(), keyStack, te, world, pos, player
                    );
                    if (key.canUnlock(ctx)) {
                        ((ILock) te.getLock().getItem()).onUnlock(ctx);
                        key.unlock(ctx);
                        te.onUnlock(ctx);
                        return ActionResultType.SUCCESS;
                    }
                }
            }
            ItemStack stack = player.getHeldItem(hand);
            if (!te.hasLock() && stack != null && stack != ItemStack.EMPTY && stack
                    .getItem() instanceof ILock) {
                te.setLock(stack);
                stack.setCount(stack.getCount() - 1);
                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.PASS;
    }
}
