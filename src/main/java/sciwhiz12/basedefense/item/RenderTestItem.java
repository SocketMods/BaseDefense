package sciwhiz12.basedefense.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import sciwhiz12.basedefense.net.ChangePOVPacket;
import sciwhiz12.basedefense.net.NetworkHandler;

public class RenderTestItem extends Item {
    public RenderTestItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if (target.world.isRemote) return false;
        if (target instanceof PlayerEntity) return false;

        System.out.println("TEST");
        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerIn), new ChangePOVPacket(
            target));

        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        // if (worldIn.isRemote) {
        if (!worldIn.isRemote) {
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerIn),
                new ChangePOVPacket(playerIn));
            // System.out.println(Minecraft.getInstance().renderViewEntity.toString());
        }
        return ActionResult.resultSuccess(heldItem);
    }
}
