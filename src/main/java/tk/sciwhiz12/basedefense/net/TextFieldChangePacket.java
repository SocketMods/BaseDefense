package tk.sciwhiz12.basedefense.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.container.KeysmithContainer;

import java.util.function.Supplier;

public class TextFieldChangePacket {
    public final String text;

    public TextFieldChangePacket(@Nullable String text) {
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
            @Nullable ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                if (sender.containerMenu instanceof KeysmithContainer cont) {
                    cont.setOutputName(pkt.text);
                }
            }
        });
        return true;
    }
}
