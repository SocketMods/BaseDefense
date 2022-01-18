package sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class KeyringRenderer extends ItemStackTileEntityRenderer {
    private static final double[][] transforms = { { 0.6D, 1.5D, 0.001D }, { -0.0185D, 0.75, 0D },
            { 1.645D, 1.45D, 0.002D } };
    private static final int[] rotations = { 135, 90, 180 };

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transform, MatrixStack matrixStack,
            IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
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

    public void renderItem(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight,
            int combinedOverlay) {
        matrix.pushPose();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        IBakedModel model = itemRenderer.getModel(stack, null, null);
        IVertexBuilder builder = ItemRenderer
                .getFoilBuffer(buffer, RenderTypeLookup.getRenderType(stack, false), true, stack.hasFoil());
        List<BakedQuad> quads = model.getQuads(null, null, new Random(42L), EmptyModelData.INSTANCE);
        itemRenderer.renderQuadList(matrix, builder, quads, stack, combinedLight, combinedOverlay);
        matrix.popPose();
    }
}
