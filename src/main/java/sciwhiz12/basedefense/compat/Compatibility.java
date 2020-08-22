package sciwhiz12.basedefense.compat;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import static sciwhiz12.basedefense.BaseDefense.LOG;
import static sciwhiz12.basedefense.Reference.CURIOS_MODID;
import static sciwhiz12.basedefense.Reference.MODID;

@EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public class Compatibility {
    private static final Marker COMPAT = MarkerManager.getMarker("COMPAT");

    public static boolean isLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    @SubscribeEvent
    static void sendIMC(InterModEnqueueEvent event) {
        LOG.debug(COMPAT, "Sending IMC messages to compatible mods");

        if (isLoaded(CURIOS_MODID)) {
            LOG.debug(COMPAT, "Sending IMC to Curios API ({})", CURIOS_MODID);
            InterModComms.sendTo(MODID, CURIOS_MODID, SlotTypeMessage.REGISTER_TYPE,
                    SlotTypePreset.CHARM.getMessageBuilder()::build);
        }
    }
}
