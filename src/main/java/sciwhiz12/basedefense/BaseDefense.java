package sciwhiz12.basedefense;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sciwhiz12.basedefense.client.color.IdentifyingColor;
import sciwhiz12.basedefense.client.gui.KeysmithScreen;
import sciwhiz12.basedefense.client.gui.LocksmithScreen;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.init.ModContainers;
import sciwhiz12.basedefense.init.ModItems;
import sciwhiz12.basedefense.init.ModRecipes;
import sciwhiz12.basedefense.init.ModTileEntities;
import sciwhiz12.basedefense.net.NetworkHandler;

@Mod(BaseDefense.MODID)
public class BaseDefense {
    public static final String MODID = "basedefense";

    public static final Logger LOG = LogManager.getLogger(MODID);

    public BaseDefense() {
        IEventBus MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

        NetworkHandler.registerPackets();

        ModItems.REGISTER.register(MOD_EVENT_BUS);
        ModBlocks.REGISTER.register(MOD_EVENT_BUS);
        ModTileEntities.REGISTER.register(MOD_EVENT_BUS);
        ModContainers.REGISTER.register(MOD_EVENT_BUS);
        ModRecipes.REGISTER.register(MOD_EVENT_BUS);

        MOD_EVENT_BUS.addListener(this::onClientSetup);
        MOD_EVENT_BUS.addListener(this::registerColors);
    }

    @SubscribeEvent
    void onClientSetup(FMLClientSetupEvent event) {
        ModBlocks.setupRenderLayer();
        ScreenManager.registerFactory(ModContainers.KEYSMITH_CONTAINER.get(), KeysmithScreen::new);
        ScreenManager.registerFactory(ModContainers.LOCKSMITH_CONTAINER.get(), LocksmithScreen::new);
    }

    @SubscribeEvent
    void registerColors(ColorHandlerEvent.Item event) {
        event.getItemColors().register(
            new IdentifyingColor(), ModItems.KEY.get(), ModItems.LOCK_CORE.get(), ModItems.PADLOCK.get()
        );
    }
}
