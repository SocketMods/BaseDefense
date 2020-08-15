package sciwhiz12.basedefense.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import sciwhiz12.basedefense.ClientReference.Textures;
import sciwhiz12.basedefense.container.PortableSafeContainer;

public class PortableSafeScreen extends ContainerScreen<PortableSafeContainer> {
    public PortableSafeScreen(PortableSafeContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.xSize = 176;
        this.ySize = 166;
        this.playerInventoryTitleY = this.playerInventoryTitleY - 16;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(stack, mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(Textures.PORTABLE_SAFE_GUI);
        this.blit(stack, guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
