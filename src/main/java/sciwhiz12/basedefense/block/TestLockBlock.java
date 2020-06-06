package sciwhiz12.basedefense.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class TestLockBlock extends LockableBaseBlock {
    public TestLockBlock() {
        super(Block.Properties.create(Material.IRON));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockRayTraceResult rayTrace) {
        ActionResultType result = super.onBlockActivated(state, world, pos, player, hand, rayTrace);
        if (!world.isRemote && result == ActionResultType.SUCCESS) {
            player.sendMessage(new StringTextComponent("Correct key!"));
        }
        return result;
    }
    
    // @Override
    // public boolean isUnlockAllowed(ItemStack lockStack, ItemStack keyStack, World
    // worldIn, BlockPos pos, ILockable block,
    // PlayerEntity player) {
    // return true;
    // }
}
