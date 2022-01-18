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
        super(RenderType::entityCutoutNoCull);
        texWidth = 64;
        texHeight = 64;

        feet = new ModelRenderer(this);
        feet.setPos(0.0F, 0.0F, 0.0F);
        feet.texOffs(42, 0).addBox(-6.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        feet.texOffs(42, 3).addBox(4.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        feet.texOffs(42, 6).addBox(-6.0F, 7.0F, 4.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        feet.texOffs(42, 9).addBox(4.0F, 7.0F, 4.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        walls = new ModelRenderer(this);
        walls.setPos(0.0F, 0.0F, 0.0F);
        walls.texOffs(0, 0).addBox(-7.0F, -7.0F, -7.0F, 14.0F, 2.0F, 14.0F, 0.0F, false);
        walls.texOffs(0, 0).addBox(-7.0F, 5.0F, -7.0F, 14.0F, 2.0F, 14.0F, 0.0F, true);
        walls.texOffs(32, 16).addBox(-7.0F, -5.0F, -7.0F, 2.0F, 10.0F, 14.0F, 0.0F, false);
        walls.texOffs(0, 16).addBox(5.0F, -5.0F, -7.0F, 2.0F, 10.0F, 14.0F, 0.0F, false);
        walls.texOffs(18, 16).addBox(-5.0F, -5.0F, 5.0F, 10.0F, 10.0F, 2.0F, 0.0F, false);

        door = new ModelRenderer(this);
        door.setPos(4.0F, 0.0F, -6.0F);
        door.texOffs(18, 16).addBox(-9.0F, -5.0F, -0.5F, 10.0F, 10.0F, 2.0F, 0.0F, false);
        door.texOffs(0, 0).addBox(-8.0F, -2.0F, -1.5F, 2.0F, 4.0F, 1.0F, 0.0F, false);

        top = new ModelRenderer(this);
        top.setPos(4.0F, 0.0F, -6.0F);
        top.texOffs(0, 5).addBox(-5.0F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, true);

        middle = new ModelRenderer(this);
        middle.setPos(4.0F, 0.0F, -6.0F);
        middle.texOffs(0, 8).addBox(-3.5F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        bottom = new ModelRenderer(this);
        bottom.setPos(4.0F, 0.0F, -6.0F);
        bottom.texOffs(0, 11).addBox(-2.0F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red,
            float green, float blue, float alpha) {
        matrixStack.pushPose();
        feet.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        walls.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        door.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        top.render(matrixStack, buffer, packedLight, packedOverlay, topColor[0], topColor[1], topColor[2], alpha);
        middle.render(matrixStack, buffer, packedLight, packedOverlay, middleColor[0], middleColor[1], middleColor[2],
                alpha);
        bottom.render(matrixStack, buffer, packedLight, packedOverlay, bottomColor[0], bottomColor[1], bottomColor[2],
                alpha);
        matrixStack.popPose();
    }

    public void setDoorAngle(float angle) {
        door.yRot = angle;
        top.yRot = angle;
        middle.yRot = angle;
        bottom.yRot = angle;
    }

    public void setColorsVisibility(boolean top, boolean middle, boolean bottom) {
        this.top.visible = top;
        this.middle.visible = middle;
        this.bottom.visible = bottom;
    }

    public void setColors(int top, int middle, int bottom) {
        this.topColor[0] = ColorHelper.PackedColor.red(top) / 255F;
        this.topColor[1] = ColorHelper.PackedColor.green(top) / 255F;
        this.topColor[2] = ColorHelper.PackedColor.blue(top) / 255F;
        this.middleColor[0] = ColorHelper.PackedColor.red(middle) / 255F;
        this.middleColor[1] = ColorHelper.PackedColor.green(middle) / 255F;
        this.middleColor[2] = ColorHelper.PackedColor.blue(middle) / 255F;
        this.bottomColor[0] = ColorHelper.PackedColor.red(bottom) / 255F;
        this.bottomColor[1] = ColorHelper.PackedColor.green(bottom) / 255F;
        this.bottomColor[2] = ColorHelper.PackedColor.blue(bottom) / 255F;
    }
}
