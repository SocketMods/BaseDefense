package sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import sciwhiz12.basedefense.ClientReference.Textures;
import sciwhiz12.basedefense.Reference.Blocks;
import sciwhiz12.basedefense.block.PortableSafeBlock;
import sciwhiz12.basedefense.client.model.PortableSafeModel;
import sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;

public class PortableSafeRenderer extends TileEntityRenderer<PortableSafeTileEntity> {
    private final PortableSafeModel model = new PortableSafeModel();

    public PortableSafeRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public void render(PortableSafeTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
            IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        World world = tileEntityIn.getWorld();
        boolean flag = world != null;
        BlockState blockstate = flag ?
                tileEntityIn.getBlockState() :
                Blocks.PORTABLE_SAFE.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        Block block = blockstate.getBlock();
        if (block instanceof PortableSafeBlock) {
            matrixStackIn.push();
            float f = blockstate.get(ChestBlock.FACING).getHorizontalAngle();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
            IVertexBuilder vertex = bufferIn.getBuffer(RenderType.getEntityCutout(Textures.PORTABLE_SAFE_MODEL));
            this.renderModels(matrixStackIn, vertex, tileEntityIn.getDoorAngle(partialTicks), combinedLightIn,
                    combinedOverlayIn);
            matrixStackIn.pop();
        }
    }

    private void renderModels(MatrixStack matrixStackIn, IVertexBuilder bufferIn, float doorAngle, int combinedLightIn,
            int combinedOverlayIn) {
        model.setDoorAngle((float) -(doorAngle * (Math.PI / 2F)));
        model.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
    }
}
