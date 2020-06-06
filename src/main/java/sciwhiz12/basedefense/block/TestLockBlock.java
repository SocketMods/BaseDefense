package sciwhiz12.basedefense.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import sciwhiz12.basedefense.tileentity.LockableTile;
import sciwhiz12.basedefense.util.UnlockHelper;
import sciwhiz12.basedefense.util.Util;

public class TestLockBlock extends Block {
    public TestLockBlock() {
        super(Block.Properties.create(Material.IRON));
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
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockRayTraceResult rayTrace) {
        if (!world.isRemote && world.isBlockLoaded(pos) && world.getTileEntity(pos) != null) {
            TileEntity tileEntity = world.getTileEntity(pos);
            ItemStack keyStack = player.getHeldItem(hand);
            IWorldPosCallable worldPos = Util.getOrDummy(world, pos);
            if (UnlockHelper.checkUnlock(keyStack, tileEntity, worldPos, player)) {
                UnlockHelper.consumeIfPresent(keyStack, tileEntity, (key, lock) -> {
                    key.onUnlock(lock, worldPos, player);
                    lock.onUnlock(key, worldPos, player);
                });
                player.sendMessage(new StringTextComponent("Correct key!"));
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }
}
