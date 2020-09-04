package sciwhiz12.basedefense.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ColorHelper;

public class PortableSafeModel extends Model {
    private final ModelRenderer feet;
    private final ModelRenderer walls;
    private final ModelRenderer door;
    private final ModelRenderer top;
    private final ModelRenderer middle;
    private final ModelRenderer bottom;
    public float[] topColor = new float[3], middleColor = new float[3], bottomColor = new float[3];

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
        top.setRotationPoint(4.0F, 0.0F, -6.0F);
        top.setTextureOffset(0, 5).addBox(-5.0F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, true);

        middle = new ModelRenderer(this);
        middle.setRotationPoint(4.0F, 0.0F, -6.0F);
        middle.setTextureOffset(0, 8).addBox(-3.5F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        bottom = new ModelRenderer(this);
        bottom.setRotationPoint(4.0F, 0.0F, -6.0F);
        bottom.setTextureOffset(0, 11).addBox(-2.0F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red,
            float green, float blue, float alpha) {
        matrixStack.push();
        feet.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        walls.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        door.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        top.render(matrixStack, buffer, packedLight, packedOverlay, topColor[0], topColor[1], topColor[2], alpha);
        middle.render(matrixStack, buffer, packedLight, packedOverlay, middleColor[0], middleColor[1], middleColor[2],
                alpha);
        bottom.render(matrixStack, buffer, packedLight, packedOverlay, bottomColor[0], bottomColor[1], bottomColor[2],
                alpha);
        matrixStack.pop();
    }

    public void setDoorAngle(float angle) {
        door.rotateAngleY = angle;
        top.rotateAngleY = angle;
        middle.rotateAngleY = angle;
        bottom.rotateAngleY = angle;
    }

    public void setColorsVisibility(boolean top, boolean middle, boolean bottom) {
        this.top.showModel = top;
        this.middle.showModel = middle;
        this.bottom.showModel = bottom;
    }

    public void setColors(int top, int middle, int bottom) {
        this.topColor[0] = ColorHelper.PackedColor.getRed(top) / 255F;
        this.topColor[1] = ColorHelper.PackedColor.getGreen(top) / 255F;
        this.topColor[2] = ColorHelper.PackedColor.getBlue(top) / 255F;
        this.middleColor[0] = ColorHelper.PackedColor.getRed(middle) / 255F;
        this.middleColor[1] = ColorHelper.PackedColor.getGreen(middle) / 255F;
        this.middleColor[2] = ColorHelper.PackedColor.getBlue(middle) / 255F;
        this.bottomColor[0] = ColorHelper.PackedColor.getRed(bottom) / 255F;
        this.bottomColor[1] = ColorHelper.PackedColor.getGreen(bottom) / 255F;
        this.bottomColor[2] = ColorHelper.PackedColor.getBlue(bottom) / 255F;
    }
}
