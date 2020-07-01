package sciwhiz12.basedefense.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import sciwhiz12.basedefense.net.ChangePOVPacket;
import sciwhiz12.basedefense.net.NetworkHandler;

public class RenderTestItem extends Item {
    public RenderTestItem(Properties properties) {
        super(properties);
    }

    /*
     * CAMERA TESTING ITEM
     * 
     * Right click an entity to change the renderViewEntity to that entity. Right
     * click any block to revert the renderViewEntity to the player.
     * 
     * See CameraPOVManager and ChangePOVPacket for how the renderViewEntity is
     * changed
     */

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target,
            Hand hand) {
        if (target.world.isRemote) return ActionResultType.PASS;
        if (target instanceof PlayerEntity) return ActionResultType.PASS;

        NetworkHandler.CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerIn), new ChangePOVPacket(target)
        );

        return ActionResultType.PASS;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote) {
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ChangePOVPacket(player));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
