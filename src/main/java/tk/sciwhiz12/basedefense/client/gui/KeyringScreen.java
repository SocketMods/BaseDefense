package tk.sciwhiz12.basedefense.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import tk.sciwhiz12.basedefense.ClientReference.Textures;
import tk.sciwhiz12.basedefense.container.KeyringContainer;

public class KeyringScreen extends AbstractContainerScreen<KeyringContainer> {
    public KeyringScreen(KeyringContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 130;
        this.inventoryLabelY = 37;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, Textures.KEYRING_GUI);
        this.blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
