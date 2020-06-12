package sciwhiz12.basedefense.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import sciwhiz12.basedefense.entity.PTZCameraEntity;

public class PTZCameraModel extends EntityModel<PTZCameraEntity> {
    public final ModelRenderer arm;
    public final ModelRenderer cam;
    public final ModelRenderer bb_main;

    public PTZCameraModel() {
        textureWidth = 32;
        textureHeight = 32;

        arm = new ModelRenderer(this);
        arm.setRotationPoint(0.0F, 14.0F, 7.5F);
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

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
    }

    @Override
    public void setRotationAngles(PTZCameraEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {
        // previously the render function, render code was moved to a method below
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red,
            float green, float blue, float alpha) {
        arm.render(matrixStack, buffer, packedLight, packedOverlay);
        bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
