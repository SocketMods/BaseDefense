package sciwhiz12.basedefense.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import sciwhiz12.basedefense.ClientReference.Textures;
import sciwhiz12.basedefense.container.LocksmithContainer;

public class LocksmithScreen extends ContainerScreen<LocksmithContainer> {
    public LocksmithScreen(LocksmithContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void func_230430_a_(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.func_230446_a_(stack);
        super.func_230430_a_(stack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(stack, mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void func_230450_a_(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_230706_i_.getTextureManager().bindTexture(Textures.LOCKSMITH_GUI);
        this.func_238474_b_(stack, guiLeft, guiTop, 0, 0, xSize, ySize);
        int iconX = 0, iconY = 166;
        if (this.container.testingState.get() == 1) iconX = 21;
        this.func_238474_b_(stack, guiLeft + 114, guiTop + 27, iconX, iconY, 21, 21);
    }
}
