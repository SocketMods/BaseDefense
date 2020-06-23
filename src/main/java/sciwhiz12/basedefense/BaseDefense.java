package sciwhiz12.basedefense;

import static sciwhiz12.basedefense.Reference.MODID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraftforge.fml.common.Mod;
import sciwhiz12.basedefense.net.NetworkHandler;

@Mod(MODID)
public class BaseDefense {
    public static final Logger LOG = LogManager.getLogger();
    public static final Marker COMMON = MarkerManager.getMarker("COMMON");
    public static final Marker CLIENT = MarkerManager.getMarker("CLIENT");
    public static final Marker SERVER = MarkerManager.getMarker("SERVER");

    public BaseDefense() {
        NetworkHandler.registerPackets();
    }
}
