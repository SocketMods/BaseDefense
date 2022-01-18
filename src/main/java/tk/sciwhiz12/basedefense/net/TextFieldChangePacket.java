package tk.sciwhiz12.basedefense.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import tk.sciwhiz12.basedefense.container.KeysmithContainer;

import java.util.function.Supplier;

public class TextFieldChangePacket {
    public final String text;

    public TextFieldChangePacket(String text) {
        this.text = text != null ? text : "";
    }

    public static void encode(TextFieldChangePacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.text);
    }

    public static TextFieldChangePacket decode(FriendlyByteBuf buf) {
        return new TextFieldChangePacket(buf.readUtf(64));
    }

    public static boolean process(TextFieldChangePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ctx.get().getDirection();
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                AbstractContainerMenu cont = sender.containerMenu;
                if (cont instanceof KeysmithContainer) {
                    ((KeysmithContainer) cont).setOutputName(pkt.text);
                }
            }
        });
        return true;
    }
}
