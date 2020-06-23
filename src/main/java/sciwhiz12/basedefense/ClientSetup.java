package sciwhiz12.basedefense;

import static sciwhiz12.basedefense.BaseDefense.CLIENT;
import static sciwhiz12.basedefense.BaseDefense.LOG;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sciwhiz12.basedefense.client.gui.KeyringScreen;
import sciwhiz12.basedefense.client.gui.KeysmithScreen;
import sciwhiz12.basedefense.client.gui.LocksmithScreen;
import sciwhiz12.basedefense.client.render.PadlockedDoorRenderer;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.init.ModContainers;
import sciwhiz12.basedefense.init.ModTileEntities;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD, modid = BaseDefense.MODID)
public class ClientSetup {
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        LOG.debug(CLIENT, "Setting up on client");

        ModBlocks.setupRenderLayer();

        LOG.debug(CLIENT, "Registering screen factories");
        ScreenManager.registerFactory(ModContainers.KEYSMITH_TABLE, KeysmithScreen::new);
        ScreenManager.registerFactory(ModContainers.LOCKSMITH_TABLE, LocksmithScreen::new);
        ScreenManager.registerFactory(ModContainers.KEYRING, KeyringScreen::new);

        LOG.debug(CLIENT, "Binding tile entity renderers");
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.PADLOCKED_DOOR, PadlockedDoorRenderer::new);
    }
}
