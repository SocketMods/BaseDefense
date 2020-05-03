package sciwhiz12.basedefense;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sciwhiz12.basedefense.client.gui.KeysmithScreen;
import sciwhiz12.basedefense.client.gui.LocksmithScreen;

@Mod(BaseDefense.MODID)
public class BaseDefense {
    public static final String MODID = "basedefense";

    public static final Logger LOG = LogManager.getLogger(MODID);

    public BaseDefense() {
        IEventBus MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        BDItems.ITEM.register(MOD_EVENT_BUS);
        BDBlocks.BLOCK.register(MOD_EVENT_BUS);
        BDBlocks.TE.register(MOD_EVENT_BUS);
        BDBlocks.CONTAINER.register(MOD_EVENT_BUS);
        MOD_EVENT_BUS.addListener(this::onClientSetupEvent);
    }

    @SubscribeEvent
    void onClientSetupEvent(FMLClientSetupEvent event) {
        BDBlocks.setupRenderLayer();
        ScreenManager.registerFactory(BDBlocks.KEYSMITH_CONTAINER.get(), KeysmithScreen::new);
        ScreenManager.registerFactory(BDBlocks.LOCKSMITH_CONTAINER.get(), LocksmithScreen::new);
    }
}
