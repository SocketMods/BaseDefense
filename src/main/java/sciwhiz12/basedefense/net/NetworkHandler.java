package sciwhiz12.basedefense.net;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import sciwhiz12.basedefense.BaseDefense;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(BaseDefense.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private static int ID = 1;

    public static void registerPackets() {
        BaseDefense.LOG.debug("Registering packets");
        CHANNEL.registerMessage(
            ID++, TextFieldChangePacket.class, TextFieldChangePacket::encode, TextFieldChangePacket::decode,
            TextFieldChangePacket::process
        );
    }
}
