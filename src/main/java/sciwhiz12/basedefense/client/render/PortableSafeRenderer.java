package sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import sciwhiz12.basedefense.ClientReference.Textures;
import sciwhiz12.basedefense.Reference.Blocks;
import sciwhiz12.basedefense.block.PortableSafeBlock;
import sciwhiz12.basedefense.client.model.PortableSafeModel;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;

public class PortableSafeRenderer extends TileEntityRenderer<PortableSafeTileEntity> {
    private final PortableSafeModel model = new PortableSafeModel();

    public PortableSafeRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public void render(PortableSafeTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
            IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        final BlockState blockstate = tileEntityIn.getLevel() != null ?
                tileEntityIn.getBlockState() :
                Blocks.PORTABLE_SAFE.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
        if (blockstate.getBlock() instanceof PortableSafeBlock) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-blockstate.getValue(ChestBlock.FACING).toYRot()));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
            IVertexBuilder vertex = bufferIn.getBuffer(RenderType.entityCutout(Textures.PORTABLE_SAFE_MODEL));
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
