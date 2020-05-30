package sciwhiz12.basedefense.client.model;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.init.ModBlocks;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModelHandler {
    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        overrideModel(event, ModBlocks.LOCKED_IRON_DOOR, LockedDoorModel::new);
        overrideModel(event, ModBlocks.LOCKED_OAK_DOOR, LockedDoorModel::new);
        overrideModel(event, ModBlocks.LOCKED_BIRCH_DOOR, LockedDoorModel::new);
        overrideModel(event, ModBlocks.LOCKED_SPRUCE_DOOR, LockedDoorModel::new);
        overrideModel(event, ModBlocks.LOCKED_JUNGLE_DOOR, LockedDoorModel::new);
        overrideModel(event, ModBlocks.LOCKED_ACACIA_DOOR, LockedDoorModel::new);
        overrideModel(event, ModBlocks.LOCKED_DARK_OAK_DOOR, LockedDoorModel::new);
    }

    private static void overrideModel(ModelBakeEvent event, Block b, Function<IBakedModel, IBakedModel> transform) {
        for (BlockState blockState : b.getStateContainer().getValidStates()) {
            ModelResourceLocation variantMRL = BlockModelShapes.getModelLocation(blockState);
            IBakedModel existingModel = event.getModelRegistry().get(variantMRL);
            if (existingModel != null) {
                IBakedModel customModel = transform.apply(existingModel);
                event.getModelRegistry().put(variantMRL, customModel);
            }
        }

    }
}
