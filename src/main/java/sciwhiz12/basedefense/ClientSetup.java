package sciwhiz12.basedefense;

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
        BaseDefense.LOG.debug("Setting up on client");
        ModBlocks.setupRenderLayer();
        ScreenManager.registerFactory(ModContainers.KEYSMITH_TABLE, KeysmithScreen::new);
        ScreenManager.registerFactory(ModContainers.LOCKSMITH_TABLE, LocksmithScreen::new);
        ScreenManager.registerFactory(ModContainers.KEYRING, KeyringScreen::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.PADLOCKED_DOOR, PadlockedDoorRenderer::new);
    }
}
