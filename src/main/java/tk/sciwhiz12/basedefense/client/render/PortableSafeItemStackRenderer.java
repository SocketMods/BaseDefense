package tk.sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import tk.sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;

import java.util.function.Supplier;

public class PortableSafeItemStackRenderer extends ItemStackTileEntityRenderer {
    private final Lazy<PortableSafeTileEntity> te;

    public PortableSafeItemStackRenderer(Supplier<PortableSafeTileEntity> te) {
        this.te = Lazy.of(te);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transforms, MatrixStack matrixStack,
            IRenderTypeBuffer buffer, int light, int overlay) {
        TileEntityRendererDispatcher.instance.renderItem(te.get(), matrixStack, buffer, light, overlay);
    }

    public static ItemStackTileEntityRenderer create() {
        return new PortableSafeItemStackRenderer(PortableSafeTileEntity::new);
    }
}
