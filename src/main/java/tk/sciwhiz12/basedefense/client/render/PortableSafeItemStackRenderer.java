package tk.sciwhiz12.basedefense.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import tk.sciwhiz12.basedefense.Reference;
import tk.sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;

import java.util.function.Supplier;

public class PortableSafeItemStackRenderer extends BlockEntityWithoutLevelRenderer {
    private final Lazy<PortableSafeTileEntity> te;
    private final BlockEntityRenderDispatcher dispatcher;

    public PortableSafeItemStackRenderer(Supplier<PortableSafeTileEntity> te) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        this.te = Lazy.of(te);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transforms, PoseStack matrixStack,
            MultiBufferSource buffer, int light, int overlay) {
        dispatcher.renderItem(te.get(), matrixStack, buffer, light, overlay);
    }

    public static BlockEntityWithoutLevelRenderer create() {
        return new PortableSafeItemStackRenderer(() -> new PortableSafeTileEntity(BlockPos.ZERO, Reference.Blocks.PORTABLE_SAFE.defaultBlockState()));
    }
}
