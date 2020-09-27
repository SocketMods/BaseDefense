package sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;
import sciwhiz12.basedefense.ClientReference.Textures;
import sciwhiz12.basedefense.block.PTZCameraBlock;
import sciwhiz12.basedefense.client.model.PTZCameraModel;
import sciwhiz12.basedefense.tileentity.PTZCameraTile;

public class PTZCameraRenderer extends TileEntityRenderer<PTZCameraTile> {
    private final PTZCameraModel model = new PTZCameraModel();

    public PTZCameraRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(PTZCameraTile tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer,
            int combinedLight, int combinedOverlay) {
        matrixStack.push();

        matrixStack.translate(0.5D, 1.501D, 0.5D);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(180));

        BlockState state = tileEntity.getBlockState();
        if (state.getBlock() instanceof PTZCameraBlock) {
            matrixStack.rotate(Vector3f.YP.rotationDegrees(state.get(PTZCameraBlock.FACING).getHorizontalAngle()));
            model.cam.rotateAngleY = (float) tileEntity.getRenderYaw();
            model.arm.rotateAngleX = (float) (-tileEntity.getPitch() + 0.4363F);
        }

        IVertexBuilder vertex = buffer.getBuffer(RenderType.getEntitySolid(Textures.PTZ_CAMERA_MODEL));
        model.render(matrixStack, vertex, combinedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

        matrixStack.pop();
    }
}
