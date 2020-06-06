package sciwhiz12.basedefense.init;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.client.model.ISTERWrapper;
import sciwhiz12.basedefense.client.model.LockedDoorModel;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModModels {
    
    @SubscribeEvent
    static void onModelBake(ModelBakeEvent event) {
        Block[] doorBlocks = { ModBlocks.LOCKED_IRON_DOOR, ModBlocks.LOCKED_OAK_DOOR, ModBlocks.LOCKED_BIRCH_DOOR,
                ModBlocks.LOCKED_SPRUCE_DOOR, ModBlocks.LOCKED_JUNGLE_DOOR, ModBlocks.LOCKED_ACACIA_DOOR,
                ModBlocks.LOCKED_DARK_OAK_DOOR };
        for (Block b : doorBlocks) { overrideBlockModel(event, b, LockedDoorModel::new); }

        overrideItemModel(event, ModItems.KEYRING, ISTERWrapper::new);
    }

    private static void overrideBlockModel(ModelBakeEvent event, Block b, Function<IBakedModel, IBakedModel> transform) {
        for (BlockState blockState : b.getStateContainer().getValidStates()) {
            ModelResourceLocation variantMRL = BlockModelShapes.getModelLocation(blockState);
            overrideModel(event, variantMRL, transform);
        }
    }

    private static void overrideItemModel(ModelBakeEvent event, Item i, Function<IBakedModel, IBakedModel> transform) {
        overrideModel(event, new ModelResourceLocation(i.getRegistryName(), "inventory"), transform);
    }

    private static void overrideModel(ModelBakeEvent event, ModelResourceLocation mrl,
            Function<IBakedModel, IBakedModel> transform) {
        IBakedModel existingModel = event.getModelRegistry().get(mrl);
        if (existingModel != null) {
            IBakedModel transformedModel = transform.apply(existingModel);
            event.getModelRegistry().put(mrl, transformedModel);
        }
    }
}
