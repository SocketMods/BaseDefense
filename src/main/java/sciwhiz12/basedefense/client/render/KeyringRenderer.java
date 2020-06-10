package sciwhiz12.basedefense.client.render;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

public class KeyringRenderer extends ItemStackTileEntityRenderer {
    private static final double[][] transforms = { { 0.6D, 1.5D, 0.001D }, { -0.0185D, 0.75, 0D }, { 1.645D, 1.45D,
            0.002D } };
    private static final int[] rotations = { 135, 90, 180 };

    public void render(ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight,
            int combinedOverlay) {
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
            int keys = 0;
            for (int i = 0; i < handler.getSlots() && keys < 3; i++) {
                ItemStack keyStack = handler.getStackInSlot(i);
                if (!keyStack.isEmpty()) {
                    matrixStack.push();
                    matrixStack.translate(0D, 0D, 0.15D);
                    matrixStack.scale(0.7F, 0.7F, 0.7F);
                    matrixStack.translate(transforms[keys][0], transforms[keys][1], transforms[keys][2]);
                    matrixStack.rotate(Vector3f.ZN.rotationDegrees(rotations[keys]));
                    this.renderItem(keyStack, matrixStack, buffer, combinedLight, combinedOverlay);
                    matrixStack.pop();
                    keys++;
                }
            }
        });
        this.renderItem(stack, matrixStack, buffer, combinedLight, combinedOverlay);
    }

    public void renderItem(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight,
            int combinedOverlay) {
        matrix.push();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        IBakedModel model = itemRenderer.getItemModelWithOverrides(stack, null, null);
        IVertexBuilder builder = ItemRenderer.getBuffer(buffer, RenderTypeLookup.getRenderType(stack), true, stack
            .hasEffect());
        itemRenderer.renderQuads(matrix, builder, model.getQuads(null, null, new Random(42L)), stack, combinedLight,
            combinedOverlay);
        matrix.pop();
    }
}
