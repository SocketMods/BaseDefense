package sciwhiz12.basedefense.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class PortableSafeModel extends Model {
    private final ModelRenderer feet;
    private final ModelRenderer walls;
    private final ModelRenderer door;
    public int topColor;
    public int middleColor;
    public int bottomColor;
    private final ModelRenderer top;
    private final ModelRenderer middle;
    private final ModelRenderer bottom;

    public PortableSafeModel() {
        super(RenderType::getEntityCutoutNoCull);
        textureWidth = 64;
        textureHeight = 64;

        feet = new ModelRenderer(this);
        feet.setRotationPoint(0.0F, 0.0F, 0.0F);
        feet.setTextureOffset(42, 0).addBox(-6.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        feet.setTextureOffset(42, 3).addBox(4.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        feet.setTextureOffset(42, 6).addBox(-6.0F, 7.0F, 4.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        feet.setTextureOffset(42, 9).addBox(4.0F, 7.0F, 4.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        walls = new ModelRenderer(this);
        walls.setRotationPoint(0.0F, 0.0F, 0.0F);
        walls.setTextureOffset(0, 0).addBox(-7.0F, -7.0F, -7.0F, 14.0F, 2.0F, 14.0F, 0.0F, false);
        walls.setTextureOffset(0, 0).addBox(-7.0F, 5.0F, -7.0F, 14.0F, 2.0F, 14.0F, 0.0F, true);
        walls.setTextureOffset(32, 16).addBox(-7.0F, -5.0F, -7.0F, 2.0F, 10.0F, 14.0F, 0.0F, false);
        walls.setTextureOffset(0, 16).addBox(5.0F, -5.0F, -7.0F, 2.0F, 10.0F, 14.0F, 0.0F, false);
        walls.setTextureOffset(18, 16).addBox(-5.0F, -5.0F, 5.0F, 10.0F, 10.0F, 2.0F, 0.0F, false);

        door = new ModelRenderer(this);
        door.setRotationPoint(4.0F, 0.0F, -6.0F);
        door.setTextureOffset(18, 16).addBox(-9.0F, -5.0F, -0.5F, 10.0F, 10.0F, 2.0F, 0.0F, false);
        door.setTextureOffset(0, 0).addBox(-8.0F, -2.0F, -1.5F, 2.0F, 4.0F, 1.0F, 0.0F, false);

        top = new ModelRenderer(this);
        top.setRotationPoint(-8.0F, 0.0F, 8.0F);
        top.setTextureOffset(0, 5).addBox(3.0F, -1.0F, -9.0F, 1.0F, 2.0F, 1.0F, 0.0F, true);

        middle = new ModelRenderer(this);
        middle.setRotationPoint(-8.0F, 0.0F, 8.0F);
        middle.setTextureOffset(0, 8).addBox(4.5F, -1.0F, -9.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        bottom = new ModelRenderer(this);
        bottom.setRotationPoint(-8.0F, 0.0F, 8.0F);
        bottom.setTextureOffset(0, 11).addBox(6.0F, -1.0F, -9.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red,
            float green, float blue, float alpha) {
        matrixStack.push();
        feet.render(matrixStack, buffer, packedLight, packedOverlay);
        walls.render(matrixStack, buffer, packedLight, packedOverlay);
        door.render(matrixStack, buffer, packedLight, packedOverlay);
        matrixStack.pop();
    }

    public void setDoorAngle(float angle) {
        door.rotateAngleY = angle;
        top.rotateAngleY = angle;
        middle.rotateAngleY = angle;
        bottom.rotateAngleY = angle;
    }
}
