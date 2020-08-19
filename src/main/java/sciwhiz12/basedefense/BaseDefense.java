package sciwhiz12.basedefense;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import sciwhiz12.basedefense.net.NetworkHandler;

import static sciwhiz12.basedefense.Reference.MODID;

@Mod(MODID)
public class BaseDefense {
    public static final Logger LOG = LogManager.getLogger();
    public static final Marker COMMON = MarkerManager.getMarker("COMMON");
    public static final Marker CLIENT = MarkerManager.getMarker("CLIENT");
    public static final Marker SERVER = MarkerManager.getMarker("SERVER");

    public BaseDefense() {
        NetworkHandler.registerPackets();
        Registration.registerListeners(FMLJavaModLoadingContext.get().getModEventBus(), MinecraftForge.EVENT_BUS);
        Config.registerConfigs(ModLoadingContext.get()::registerConfig);
    }
}
