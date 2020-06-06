package sciwhiz12.basedefense;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.client.gui.KeyringScreen;
import sciwhiz12.basedefense.client.gui.KeysmithScreen;
import sciwhiz12.basedefense.client.gui.LocksmithScreen;
import sciwhiz12.basedefense.client.render.PadlockedDoorRenderer;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.init.ModContainers;
import sciwhiz12.basedefense.init.ModItems;
import sciwhiz12.basedefense.init.ModTileEntities;
import sciwhiz12.basedefense.item.IColorable;

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

    @SubscribeEvent
    static void registerItemColors(ColorHandlerEvent.Item event) {
        BaseDefense.LOG.debug("Registering item colors");
        event.getItemColors().register(IColorable.ITEM_COLOR, ModItems.KEY, ModItems.LOCK_CORE, ModItems.PADLOCK);
    }

    @SubscribeEvent
    static void registerBlockColors(ColorHandlerEvent.Block event) {
        BaseDefense.LOG.debug("Registering block colors");
        event.getBlockColors().register(LockedDoorBlock.COLOR, ModBlocks.LOCKED_IRON_DOOR, ModBlocks.LOCKED_OAK_DOOR,
            ModBlocks.LOCKED_BIRCH_DOOR, ModBlocks.LOCKED_SPRUCE_DOOR, ModBlocks.LOCKED_JUNGLE_DOOR,
            ModBlocks.LOCKED_ACACIA_DOOR, ModBlocks.LOCKED_DARK_OAK_DOOR);
    }
}
