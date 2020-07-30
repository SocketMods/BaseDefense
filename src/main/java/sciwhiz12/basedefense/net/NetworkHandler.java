package sciwhiz12.basedefense.net;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import static sciwhiz12.basedefense.BaseDefense.COMMON;
import static sciwhiz12.basedefense.BaseDefense.LOG;
import static sciwhiz12.basedefense.Reference.MODID;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry
            .newSimpleChannel(new ResourceLocation(MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
                    PROTOCOL_VERSION::equals);

    private static int ID = 1;

    public static void registerPackets() {
        LOG.debug(COMMON, "Registering packets");
        CHANNEL.messageBuilder(TextFieldChangePacket.class, ID++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(TextFieldChangePacket::encode).decoder(TextFieldChangePacket::decode)
                .consumer(TextFieldChangePacket::process).add();
    }
}
