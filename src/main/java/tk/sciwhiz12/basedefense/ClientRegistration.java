package tk.sciwhiz12.basedefense;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tk.sciwhiz12.basedefense.client.gui.KeyringScreen;
import tk.sciwhiz12.basedefense.client.gui.KeysmithScreen;
import tk.sciwhiz12.basedefense.client.gui.LocksmithScreen;
import tk.sciwhiz12.basedefense.client.gui.PortableSafeScreen;
import tk.sciwhiz12.basedefense.client.model.ISTERWrapper;
import tk.sciwhiz12.basedefense.client.model.LockedDoorModel;
import tk.sciwhiz12.basedefense.client.model.PortableSafeModel;
import tk.sciwhiz12.basedefense.client.render.KeyringLayer;
import tk.sciwhiz12.basedefense.client.render.PadlockedDoorRenderer;
import tk.sciwhiz12.basedefense.client.render.PortableSafeRenderer;

import java.util.function.Function;

import static tk.sciwhiz12.basedefense.Reference.Blocks;
import static tk.sciwhiz12.basedefense.Reference.Containers;
import static tk.sciwhiz12.basedefense.Reference.Items;
import static tk.sciwhiz12.basedefense.Reference.MODID;
import static tk.sciwhiz12.basedefense.Reference.TileEntities;

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
        setupRenderLayer();
        event.enqueueWork(ClientRegistration::registerPropertyOverrides);
        event.enqueueWork(ClientRegistration::registerScreenFactories);
    }

    static void registerPropertyOverrides() {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Registering item property overrides");

        ItemProperties.register(Items.KEY, ClientReference.PropertyOverrides.COLORS, ClientReference.PropertyOverrides.COLORS_GETTER);
        ItemProperties.register(Items.LOCK_CORE, ClientReference.PropertyOverrides.COLORS, ClientReference.PropertyOverrides.COLORS_GETTER);
        ItemProperties.register(Items.PADLOCK, ClientReference.PropertyOverrides.COLORS, ClientReference.PropertyOverrides.COLORS_GETTER);
        ItemProperties.register(Items.BROKEN_LOCK_PIECES, ClientReference.PropertyOverrides.COLORS, ClientReference.PropertyOverrides.COLORS_GETTER);
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

    @SubscribeEvent
    static void addCustomLayerRenderers(EntityRenderersEvent.AddLayers event) {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Adding custom player layer renderers");
        final PlayerRenderer defaultRenderer = event.getSkin("default");
        defaultRenderer.addLayer(new KeyringLayer<>(defaultRenderer));
        final PlayerRenderer slimRenderer = event.getSkin("slim");
        slimRenderer.addLayer(new KeyringLayer<>(slimRenderer));
    }

    static void registerScreenFactories() {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Registering screen factories");
        MenuScreens.register(Containers.KEYSMITH_TABLE, KeysmithScreen::new);
        MenuScreens.register(Containers.LOCKSMITH_TABLE, LocksmithScreen::new);
        MenuScreens.register(Containers.KEYRING, KeyringScreen::new);
        MenuScreens.register(Containers.PORTABLE_SAFE, PortableSafeScreen::new);
    }

    @SubscribeEvent
    static void bindTileEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Binding tile entity renderers");
        event.registerBlockEntityRenderer(TileEntities.PADLOCKED_DOOR, (ctx) -> new PadlockedDoorRenderer());
        event.registerBlockEntityRenderer(TileEntities.PORTABLE_SAFE, PortableSafeRenderer::new);
    }

    @SubscribeEvent
    static void onTextureStitchPre(TextureStitchEvent.Pre event) {
        ResourceLocation mapLoc = event.getAtlas().location();
        if (mapLoc.equals(ClientReference.Textures.ATLAS_BLOCKS_TEXTURE)) {
            BaseDefense.LOG.debug(BaseDefense.CLIENT, "Adding sprites to atlas: {}", mapLoc);
            for (ResourceLocation spriteLoc : ClientReference.Textures.SPRITE_LIST) { event.addSprite(spriteLoc); }
        }
    }

    @SubscribeEvent
    static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ClientReference.ModelLayers.PORTABLE_SAFE, PortableSafeModel::createLayerDefinition);
    }

    static void setupRenderLayer() {
        BaseDefense.LOG.debug(BaseDefense.CLIENT, "Setting up block render layers");
        final RenderType solid = RenderType.solid();
        ItemBlockRenderTypes.setRenderLayer(Blocks.KEYSMITH_TABLE, solid);
        ItemBlockRenderTypes.setRenderLayer(Blocks.LOCKSMITH_TABLE, solid);
        final RenderType cutoutMipped = RenderType.cutoutMipped();
        ItemBlockRenderTypes.setRenderLayer(Blocks.PADLOCKED_IRON_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.PADLOCKED_OAK_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.PADLOCKED_BIRCH_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.PADLOCKED_SPRUCE_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.PADLOCKED_JUNGLE_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.PADLOCKED_ACACIA_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.PADLOCKED_DARK_OAK_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.LOCKED_IRON_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.LOCKED_OAK_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.LOCKED_BIRCH_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.LOCKED_SPRUCE_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.LOCKED_JUNGLE_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.LOCKED_ACACIA_DOOR, cutoutMipped);
        ItemBlockRenderTypes.setRenderLayer(Blocks.LOCKED_DARK_OAK_DOOR, cutoutMipped);
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

    static void overrideBlockModel(ModelBakeEvent event, Block b, Function<BakedModel, BakedModel> transform) {
        for (BlockState blockState : b.getStateDefinition().getPossibleStates()) {
            ModelResourceLocation variantMRL = BlockModelShaper.stateToModelLocation(blockState);
            overrideModel(event, variantMRL, transform);
        }
    }

    static void overrideItemModel(ModelBakeEvent event, Item i, Function<BakedModel, BakedModel> transform) {
        overrideModel(event, new ModelResourceLocation(i.getRegistryName(), "inventory"), transform);
    }

    static void overrideModel(ModelBakeEvent event, ModelResourceLocation mrl,
            Function<BakedModel, BakedModel> transform) {
        BakedModel existingModel = event.getModelRegistry().get(mrl);
        if (existingModel != null) {
            BakedModel transformedModel = transform.apply(existingModel);
            event.getModelRegistry().put(mrl, transformedModel);
        }
    }
}
