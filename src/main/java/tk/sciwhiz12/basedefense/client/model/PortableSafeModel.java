package tk.sciwhiz12.basedefense.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;

public class PortableSafeModel extends Model {
    private final ModelPart feet;
    private final ModelPart walls;
    private final ModelPart door;
    private final ModelPart top;
    private final ModelPart middle;
    private final ModelPart bottom;
    public float[] topColor = new float[3], middleColor = new float[3], bottomColor = new float[3];

    public PortableSafeModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);

        this.feet = root.getChild("feet");
        this.walls = root.getChild("walls");
        this.door = root.getChild("door");
        this.top = root.getChild("top");
        this.middle = root.getChild("middle");
        this.bottom = root.getChild("bottom");
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("feet", CubeListBuilder.create()
                .texOffs(42, 0).addBox(-6.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, false)
                .texOffs(42, 3).addBox(4.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, false)
                .texOffs(42, 6).addBox(-6.0F, 7.0F, 4.0F, 2.0F, 1.0F, 2.0F, false)
                .texOffs(42, 9).addBox(4.0F, 7.0F, 4.0F, 2.0F, 1.0F, 2.0F, false),
            PartPose.ZERO);

        part.addOrReplaceChild("walls", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-7.0F, -7.0F, -7.0F, 14.0F, 2.0F, 14.0F, false)
                .texOffs(0, 0).addBox(-7.0F, 5.0F, -7.0F, 14.0F, 2.0F, 14.0F, true)
                .texOffs(32, 16).addBox(-7.0F, -5.0F, -7.0F, 2.0F, 10.0F, 14.0F, false)
                .texOffs(0, 16).addBox(5.0F, -5.0F, -7.0F, 2.0F, 10.0F, 14.0F, false)
                .texOffs(18, 16).addBox(-5.0F, -5.0F, 5.0F, 10.0F, 10.0F, 2.0F, false),
            PartPose.ZERO);

        part.addOrReplaceChild("door", CubeListBuilder.create()
                .texOffs(18, 16).addBox(-9.0F, -5.0F, -0.5F, 10.0F, 10.0F, 2.0F, false)
                .texOffs(0, 0).addBox(-8.0F, -2.0F, -1.5F, 2.0F, 4.0F, 1.0F, false),
            PartPose.offset(4.0F, 0.0F, -6.0F));

        part.addOrReplaceChild("top", CubeListBuilder.create()
                .texOffs(0, 5).addBox(-5.0F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, true),
            PartPose.offset(4.0F, 0.0F, -6.0F));

        part.addOrReplaceChild("middle", CubeListBuilder.create()
                .texOffs(0, 8).addBox(-3.5F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, false),
            PartPose.offset(4.0F, 0.0F, -6.0F));

        part.addOrReplaceChild("bottom", CubeListBuilder.create()
                .texOffs(0, 11).addBox(-2.0F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, false),
            PartPose.offset(4.0F, 0.0F, -6.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red,
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
        this.topColor[0] = FastColor.ARGB32.red(top) / 255F;
        this.topColor[1] = FastColor.ARGB32.green(top) / 255F;
        this.topColor[2] = FastColor.ARGB32.blue(top) / 255F;
        this.middleColor[0] = FastColor.ARGB32.red(middle) / 255F;
        this.middleColor[1] = FastColor.ARGB32.green(middle) / 255F;
        this.middleColor[2] = FastColor.ARGB32.blue(middle) / 255F;
        this.bottomColor[0] = FastColor.ARGB32.red(bottom) / 255F;
        this.bottomColor[1] = FastColor.ARGB32.green(bottom) / 255F;
        this.bottomColor[2] = FastColor.ARGB32.blue(bottom) / 255F;
    }
}
