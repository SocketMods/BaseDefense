package tk.sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;
import tk.sciwhiz12.basedefense.block.PadlockedDoorBlock;
import tk.sciwhiz12.basedefense.block.PadlockedDoorBlock.DoorSide;
import tk.sciwhiz12.basedefense.tileentity.PadlockedDoorTile;

public class PadlockedDoorRenderer implements BlockEntityRenderer<PadlockedDoorTile> {
    private final ItemRenderer itemRenderer;

    public PadlockedDoorRenderer() {
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(PadlockedDoorTile tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
            int combinedLightIn, int combinedOverlayIn) {
        matrixStack.pushPose();
        BlockState state = tileEntity.getBlockState();
        ItemStack itemstack = tileEntity.getLockStack();
        Direction dir = state.getValue(PadlockedDoorBlock.FACING);
        DoorHingeSide hinge = state.getValue(PadlockedDoorBlock.HINGE);
        double mult = (hinge == DoorHingeSide.RIGHT ? 1D : -1D) * (dir.getAxis() == Direction.Axis.Z ? 1D : -1D);
        matrixStack.translate(dir.getStepX() * -0.51D + 0.5D, 1D, dir.getStepZ() * -0.51D + 0.5D);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(dir.toYRot()));
        matrixStack.translate(mult * 0.275D, 0D, 0D);
        if (state.getValue(PadlockedDoorBlock.SIDE) == DoorSide.INSIDE) {
            matrixStack.translate(0D, 0D, dir.getAxis() == Direction.Axis.X ? -0.205D : 0.205D);
        }
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        this.itemRenderer.renderStatic(null, itemstack, ItemTransforms.TransformType.FIXED, false,
                matrixStack, buffer,tileEntity.getLevel(), combinedLightIn, combinedOverlayIn, 0);
        matrixStack.popPose();
    }
}
