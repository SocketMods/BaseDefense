package sciwhiz12.basedefense;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BaseDefense.MODID)
public class BaseDefense {
    public static final String MODID = "basedefense";

    public static final Logger LOG = LogManager.getLogger(MODID);

    public BaseDefense() {
        IEventBus MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        BDItems.REGISTER.register(MOD_EVENT_BUS);
        BDBlocks.BLOCKS.register(MOD_EVENT_BUS);
        BDBlocks.TE.register(MOD_EVENT_BUS);
    }

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        BDBlocks.setupRenderLayer();
    }
}
