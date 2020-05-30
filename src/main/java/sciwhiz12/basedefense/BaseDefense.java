package sciwhiz12.basedefense;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.client.gui.KeysmithScreen;
import sciwhiz12.basedefense.client.gui.LocksmithScreen;
import sciwhiz12.basedefense.client.render.PadlockedDoorRenderer;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.init.ModContainers;
import sciwhiz12.basedefense.init.ModItems;
import sciwhiz12.basedefense.init.ModTileEntities;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.net.NetworkHandler;

@Mod(BaseDefense.MODID)
public class BaseDefense {
    public static final String MODID = "basedefense";

    public static final Logger LOG = LogManager.getLogger(MODID);

    public BaseDefense() {
        IEventBus MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

        NetworkHandler.registerPackets();

        MOD_EVENT_BUS.addListener(this::onClientSetup);
        MOD_EVENT_BUS.addListener(this::registerItemColors);
        MOD_EVENT_BUS.addListener(this::registerBlockColors);
    }

    @SubscribeEvent
    void onClientSetup(FMLClientSetupEvent event) {
        ModBlocks.setupRenderLayer();
        ScreenManager.registerFactory(ModContainers.KEYSMITH_TABLE, KeysmithScreen::new);
        ScreenManager.registerFactory(ModContainers.LOCKSMITH_TABLE, LocksmithScreen::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.PADLOCKED_DOOR, PadlockedDoorRenderer::new);
    }

    @SubscribeEvent
    void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().register(IColorable.ITEM_COLOR, ModItems.KEY, ModItems.LOCK_CORE, ModItems.PADLOCK);
    }

    @SubscribeEvent
    void registerBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().register(
            LockedDoorBlock.COLOR, ModBlocks.LOCKED_IRON_DOOR, ModBlocks.LOCKED_OAK_DOOR, ModBlocks.LOCKED_BIRCH_DOOR,
            ModBlocks.LOCKED_SPRUCE_DOOR, ModBlocks.LOCKED_JUNGLE_DOOR, ModBlocks.LOCKED_ACACIA_DOOR,
            ModBlocks.LOCKED_DARK_OAK_DOOR
        );
    }
}
