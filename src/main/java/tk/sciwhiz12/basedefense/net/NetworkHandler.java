package tk.sciwhiz12.basedefense.net;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tk.sciwhiz12.basedefense.BaseDefense;
import tk.sciwhiz12.basedefense.Reference;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry
        .newSimpleChannel(new ResourceLocation(Reference.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private static int ID = 1;

    public static void registerPackets() {
        BaseDefense.LOG.debug(BaseDefense.COMMON, "Registering packets");
        CHANNEL.messageBuilder(TextFieldChangePacket.class, ID++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(TextFieldChangePacket::encode).decoder(TextFieldChangePacket::decode)
            .consumer(TextFieldChangePacket::process).add();
    }
}
