package tk.sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class KeyringRenderer extends BlockEntityWithoutLevelRenderer {
    private static final double[][] transforms = {{0.6D, 1.5D, 0.001D}, {-0.0185D, 0.75, 0D},
        {1.645D, 1.45D, 0.002D}};
    private static final int[] rotations = {135, 90, 180};

    public KeyringRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transform, PoseStack matrixStack,
                             MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        stack.getCapability(ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            int keys = 0;
            for (int i = 0; i < handler.getSlots() && keys < 3; i++) {
                ItemStack keyStack = handler.getStackInSlot(i);
                if (!keyStack.isEmpty()) {
                    matrixStack.pushPose();
                    matrixStack.translate(0D, 0D, 0.15D);
                    matrixStack.scale(0.7F, 0.7F, 0.7F);
                    matrixStack.translate(transforms[keys][0], transforms[keys][1], transforms[keys][2]);
                    matrixStack.mulPose(Vector3f.ZN.rotationDegrees(rotations[keys]));
                    this.renderItem(keyStack, matrixStack, buffer, combinedLight, combinedOverlay);
                    matrixStack.popPose();
                    keys++;
                }
            }
        });
        this.renderItem(stack, matrixStack, buffer, combinedLight, combinedOverlay);
    }

    public void renderItem(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int combinedLight,
                           int combinedOverlay) {
        matrix.pushPose();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = itemRenderer.getModel(stack, null, null, 0);
        VertexConsumer builder = ItemRenderer
            .getFoilBuffer(buffer, ItemBlockRenderTypes.getRenderType(stack, false), true, stack.hasFoil());
        List<BakedQuad> quads = model.getQuads(null, null, new Random(42L), EmptyModelData.INSTANCE);
        itemRenderer.renderQuadList(matrix, builder, quads, stack, combinedLight, combinedOverlay);
        matrix.popPose();
    }
}
