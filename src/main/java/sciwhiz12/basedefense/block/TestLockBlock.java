package sciwhiz12.basedefense.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import sciwhiz12.basedefense.item.lock.PadlockItem;
import sciwhiz12.basedefense.tileentity.TestLockTile;

public class TestLockBlock extends LockableBaseBlock {
    public TestLockBlock() {
        super(Block.Properties.create(Material.IRON));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TestLockTile();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace) {
        ActionResultType result = super.onBlockActivated(state, world, pos, player, hand, rayTrace);
        if (!world.isRemote && result == ActionResultType.SUCCESS) {
            player.sendMessage(new StringTextComponent("Correct key!"));
        }
        return result;
    }

    @Override
    public boolean isValidLock(ItemStack stack) {
        return stack.getItem() instanceof PadlockItem;
    }
}
