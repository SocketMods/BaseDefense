package sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import sciwhiz12.basedefense.block.PTZCameraBlock;
import sciwhiz12.basedefense.client.model.PTZCameraModel;
import sciwhiz12.basedefense.init.ModTextures;
import sciwhiz12.basedefense.tileentity.PTZCameraTile;

public class PTZCameraRenderer extends TileEntityRenderer<PTZCameraTile> {
    private PTZCameraModel model = new PTZCameraModel();

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
        }
        long gameTime = tileEntity.getWorld().getGameTime();
        int cycle = 65;
        long clampedTime = gameTime % (cycle * 2);
        double rotation = Math.sin(clampedTime * (Math.PI / cycle)) * 0.9;
        float rot = (float) ((Math.ceil(rotation * 100D)) / 100D);
        model.cam.rotateAngleY = rot;

        IVertexBuilder vertex = buffer.getBuffer(RenderType.getEntitySolid(ModTextures.PTZ_CAMERA_MODEL));
        model.render(matrixStack, vertex, combinedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

        matrixStack.pop();
    }
}
