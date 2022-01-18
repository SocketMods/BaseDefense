package tk.sciwhiz12.basedefense;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tk.sciwhiz12.basedefense.client.gui.KeyringScreen;
import tk.sciwhiz12.basedefense.client.gui.KeysmithScreen;
import tk.sciwhiz12.basedefense.client.gui.LocksmithScreen;
import tk.sciwhiz12.basedefense.client.gui.PortableSafeScreen;
import tk.sciwhiz12.basedefense.client.model.ISTERWrapper;
import tk.sciwhiz12.basedefense.client.model.LockedDoorModel;
import tk.sciwhiz12.basedefense.client.render.KeyringLayer;
import tk.sciwhiz12.basedefense.client.render.PadlockedDoorRenderer;
import tk.sciwhiz12.basedefense.client.render.PortableSafeRenderer;

import java.util.Map;
import java.util.function.Function;

import static tk.sciwhiz12.basedefense.Reference.*;

/**
 * Class for registering <strong>client-side only</strong> objects of this mod.
 *
 * @author SciWhiz12
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD, modid = MODID)
public final class ClientRegistration {
    // Prevent instantiation
    private ClientRegistration() {}

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Setting up on client");
        bindTileEntityRenderers();
        setupRenderLayer();
        event.enqueueWork(ClientRegistration::addCustomLayerRenderers);
        event.enqueueWork(ClientRegistration::registerPropertyOverrides);
        event.enqueueWork(ClientRegistration::registerScreenFactories);
    }

    static void registerPropertyOverrides() {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Registering item property overrides");

        ItemModelsProperties.register(Items.KEY, ClientReference.PropertyOverrides.COLORS, ClientReference.PropertyOverrides.COLORS_GETTER);
        ItemModelsProperties.register(Items.LOCK_CORE, ClientReference.PropertyOverrides.COLORS, ClientReference.PropertyOverrides.COLORS_GETTER);
        ItemModelsProperties.register(Items.PADLOCK, ClientReference.PropertyOverrides.COLORS, ClientReference.PropertyOverrides.COLORS_GETTER);
        ItemModelsProperties.register(Items.BROKEN_LOCK_PIECES, ClientReference.PropertyOverrides.COLORS, ClientReference.PropertyOverrides.COLORS_GETTER);
    }

    @SubscribeEvent
    static void registerItemColors(ColorHandlerEvent.Item event) {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Registering item colors");
        event.getItemColors()
                .register(ClientReference.Colors.ITEM_COLOR, Items.KEY, Items.LOCK_CORE, Items.PADLOCK, Items.BROKEN_LOCK_PIECES);
    }

    @SubscribeEvent
    static void registerBlockColors(ColorHandlerEvent.Block event) {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Registering block colors");
        event.getBlockColors().register(ClientReference.Colors.LOCKED_DOOR_COLOR, Blocks.LOCKED_IRON_DOOR, Blocks.LOCKED_OAK_DOOR,
                Blocks.LOCKED_BIRCH_DOOR, Blocks.LOCKED_SPRUCE_DOOR, Blocks.LOCKED_JUNGLE_DOOR, Blocks.LOCKED_ACACIA_DOOR,
                Blocks.LOCKED_DARK_OAK_DOOR);
    }

    static void addCustomLayerRenderers() {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Adding custom player layer renderers");
        final Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
        final PlayerRenderer defaultRenderer = skinMap.get("default");
        defaultRenderer.addLayer(new KeyringLayer<>(defaultRenderer));
        final PlayerRenderer slimRenderer = skinMap.get("slim");
        slimRenderer.addLayer(new KeyringLayer<>(slimRenderer));
    }

    static void registerScreenFactories() {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Registering screen factories");
        ScreenManager.register(Containers.KEYSMITH_TABLE, KeysmithScreen::new);
        ScreenManager.register(Containers.LOCKSMITH_TABLE, LocksmithScreen::new);
        ScreenManager.register(Containers.KEYRING, KeyringScreen::new);
        ScreenManager.register(Containers.PORTABLE_SAFE, PortableSafeScreen::new);
    }

    static void bindTileEntityRenderers() {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Binding tile entity renderers");
        ClientRegistry.bindTileEntityRenderer(TileEntities.PADLOCKED_DOOR, PadlockedDoorRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileEntities.PORTABLE_SAFE, PortableSafeRenderer::new);
    }

    @SubscribeEvent
    static void onTextureStitchPre(TextureStitchEvent.Pre event) {
        ResourceLocation mapLoc = event.getMap().location();
        if (mapLoc.equals(ClientReference.Textures.ATLAS_BLOCKS_TEXTURE)) {
            BaseDefense.LOG.debug(BaseDefense.CLIENT, "Adding sprites to atlas: {}", mapLoc);
            for (ResourceLocation spriteLoc : ClientReference.Textures.SPRITE_LIST) { event.addSprite(spriteLoc); }
        }
    }

    static void setupRenderLayer() {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Setting up block render layers");
        final RenderType solid = RenderType.solid();
        RenderTypeLookup.setRenderLayer(Blocks.KEYSMITH_TABLE, solid);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKSMITH_TABLE, solid);
        final RenderType cutoutMipped = RenderType.cutoutMipped();
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_IRON_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_OAK_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_BIRCH_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_SPRUCE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_JUNGLE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_ACACIA_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_DARK_OAK_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_IRON_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_OAK_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_BIRCH_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_SPRUCE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_JUNGLE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_ACACIA_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_DARK_OAK_DOOR, cutoutMipped);
    }

    @SubscribeEvent
    static void onModelBake(ModelBakeEvent event) {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Overriding models");
        overrideBlockModel(event, Blocks.LOCKED_OAK_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_BIRCH_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_SPRUCE_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_JUNGLE_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_ACACIA_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_DARK_OAK_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_IRON_DOOR, LockedDoorModel::new);

        overrideItemModel(event, Items.KEYRING, ISTERWrapper::new);
        overrideItemModel(event, Items.PORTABLE_SAFE, ISTERWrapper::new);
    }

    static void overrideBlockModel(ModelBakeEvent event, Block b, Function<IBakedModel, IBakedModel> transform) {
        for (BlockState blockState : b.getStateDefinition().getPossibleStates()) {
            ModelResourceLocation variantMRL = BlockModelShapes.stateToModelLocation(blockState);
            overrideModel(event, variantMRL, transform);
        }
    }

    static void overrideItemModel(ModelBakeEvent event, Item i, Function<IBakedModel, IBakedModel> transform) {
        overrideModel(event, new ModelResourceLocation(i.getRegistryName(), "inventory"), transform);
    }

    static void overrideModel(ModelBakeEvent event, ModelResourceLocation mrl,
            Function<IBakedModel, IBakedModel> transform) {
        IBakedModel existingModel = event.getModelRegistry().get(mrl);
        if (existingModel != null) {
            IBakedModel transformedModel = transform.apply(existingModel);
            event.getModelRegistry().put(mrl, transformedModel);
        }
    }
}
