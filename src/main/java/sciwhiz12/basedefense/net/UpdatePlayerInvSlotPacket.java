package sciwhiz12.basedefense.net;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class UpdatePlayerInvSlotPacket {
    public int slot;
    public ItemStack stack;

    public UpdatePlayerInvSlotPacket(int slot, ItemStack stack) {
        this.slot = slot;
        this.stack = stack;
    }

    public static void encode(UpdatePlayerInvSlotPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.slot);
        buf.writeItemStack(pkt.stack);
    }

    public static UpdatePlayerInvSlotPacket decode(PacketBuffer buf) {
        return new UpdatePlayerInvSlotPacket(buf.readInt(), buf.readItemStack());
    }

    @SuppressWarnings("resource")
    public static void process(UpdatePlayerInvSlotPacket pkt, Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Minecraft.getInstance().player.inventory.setInventorySlotContents(pkt.slot, pkt.stack);
            } else if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                ServerPlayerEntity sender = ctx.get().getSender();
                sender.inventory.setInventorySlotContents(pkt.slot, pkt.stack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
