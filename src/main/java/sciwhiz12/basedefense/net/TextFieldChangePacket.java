package sciwhiz12.basedefense.net;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import sciwhiz12.basedefense.container.KeysmithContainer;

import java.util.function.Supplier;

public class TextFieldChangePacket {
    public final String text;

    public TextFieldChangePacket(String text) {
        this.text = text != null ? text : "";
    }

    public static void encode(TextFieldChangePacket pkt, PacketBuffer buf) {
        buf.writeUtf(pkt.text);
    }

    public static TextFieldChangePacket decode(PacketBuffer buf) {
        return new TextFieldChangePacket(buf.readUtf(64));
    }

    public static boolean process(TextFieldChangePacket pkt, Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ctx.get().getDirection();
            ServerPlayerEntity sender = ctx.get().getSender();
            if (sender != null) {
                Container cont = sender.containerMenu;
                if (cont instanceof KeysmithContainer) {
                    ((KeysmithContainer) cont).setOutputName(pkt.text);
                }
            }
        });
        return true;
    }
}
