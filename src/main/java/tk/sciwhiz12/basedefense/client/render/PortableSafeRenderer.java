package tk.sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import tk.sciwhiz12.basedefense.ClientReference;
import tk.sciwhiz12.basedefense.ClientReference.Textures;
import tk.sciwhiz12.basedefense.Reference.Blocks;
import tk.sciwhiz12.basedefense.block.PortableSafeBlock;
import tk.sciwhiz12.basedefense.client.model.PortableSafeModel;
import tk.sciwhiz12.basedefense.item.IColorable;
import tk.sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;

public class PortableSafeRenderer implements BlockEntityRenderer<PortableSafeTileEntity> {
    private final PortableSafeModel model;

    public PortableSafeRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new PortableSafeModel(context.bakeLayer(ClientReference.ModelLayers.PORTABLE_SAFE));
    }

    public void render(PortableSafeTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn,
            MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        final BlockState blockstate = tileEntityIn.getLevel() != null ?
                tileEntityIn.getBlockState() :
                Blocks.PORTABLE_SAFE.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
        if (blockstate.getBlock() instanceof PortableSafeBlock) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-blockstate.getValue(ChestBlock.FACING).toYRot()));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
            VertexConsumer vertex = bufferIn.getBuffer(RenderType.entityCutout(Textures.PORTABLE_SAFE_MODEL));
            this.setModelColors(tileEntityIn);
            model.setDoorAngle((float) -(tileEntityIn.getDoorAngle(partialTicks) * (Math.PI / 2F)));
            model.renderToBuffer(matrixStackIn, vertex, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
            matrixStackIn.popPose();
        }
    }

    private void setModelColors(final PortableSafeTileEntity tileEntity) {
        final ItemStack stack = tileEntity.getLockStack();
        if (!stack.isEmpty() && stack.getItem() instanceof IColorable) {
            boolean top = false, middle = false, bottom = false;
            int topColor = 0, middleColor = 0, bottomColor = 0;
            final int[] color = ((IColorable) stack.getItem()).getColors(stack);
            if (color.length >= 1) {
                topColor = color[0];
                top = true;
            }
            if (color.length >= 2) {
                middleColor = color[1];
                middle = true;
            }
            if (color.length >= 3) {
                bottomColor = color[2];
                bottom = true;
            }
            model.setColorsVisibility(top, middle, bottom);
            model.setColors(topColor, middleColor, bottomColor);
        }
    }
}
