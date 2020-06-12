package sciwhiz12.basedefense.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;

public class BlankRenderer extends EntityRenderer<PTZCameraEntity> {
    public BlankRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(PTZCameraEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
            IRenderTypeBuffer bufferIn, int packedLightIn) {}

    public ResourceLocation getEntityTexture(PTZCameraEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
