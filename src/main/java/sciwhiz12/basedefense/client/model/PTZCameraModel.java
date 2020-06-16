package sciwhiz12.basedefense.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import sciwhiz12.basedefense.entity.PTZCameraEntity;

public class PTZCameraModel extends EntityModel<PTZCameraEntity> {
    public final ModelRenderer baseplate;
    public final ModelRenderer arm;
    public final ModelRenderer cam;

    public PTZCameraModel() {
        textureWidth = 32;
        textureHeight = 32;

        baseplate = new ModelRenderer(this);
        baseplate.setRotationPoint(0.0F, 13.5F, 7.0F);
        baseplate.setTextureOffset(0, 14).addBox(-3.0F, -2.5F, -0.5F, 6.0F, 5.0F, 2.0F, 0.0F, false);

        arm = new ModelRenderer(this);
        arm.setRotationPoint(0.0F, 0.5F, 0.5F);
        baseplate.addChild(arm);
        setRotationAngle(arm, 0.4363F, 0.0F, 0.0F);
        arm.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);

        cam = new ModelRenderer(this);
        cam.setRotationPoint(0.0F, -1.0F, -4.0F);
        arm.addChild(cam);
        cam.setTextureOffset(4, 0).addBox(-2.0F, -3.0F, -8.0F, 4.0F, 4.0F, 10.0F, 0.0F, false);
        cam.setTextureOffset(0, 7).addBox(-2.0F, -3.0F, -9.0F, 4.0F, 0.0F, 1.0F, 0.0F, false);
        cam.setTextureOffset(11, 0).addBox(-2.0F, -3.0F, -9.0F, 0.0F, 3.0F, 1.0F, 0.0F, false);
        cam.setTextureOffset(9, 0).addBox(2.0F, -3.0F, -9.0F, 0.0F, 3.0F, 1.0F, 0.0F, false);
        cam.setTextureOffset(22, 0).addBox(-1.0F, -2.0F, -8.5F, 2.0F, 2.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(PTZCameraEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {}

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red,
            float green, float blue, float alpha) {
        baseplate.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
