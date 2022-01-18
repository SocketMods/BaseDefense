package tk.sciwhiz12.basedefense.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;
import tk.sciwhiz12.basedefense.ClientReference.Textures;
import tk.sciwhiz12.basedefense.Reference;
import tk.sciwhiz12.basedefense.container.KeysmithContainer;
import tk.sciwhiz12.basedefense.net.NetworkHandler;
import tk.sciwhiz12.basedefense.net.TextFieldChangePacket;

public class KeysmithScreen extends AbstractContainerScreen<KeysmithContainer> implements ContainerListener {
    private EditBox nameField;
    private boolean isEnabledText = false;

    public KeysmithScreen(KeysmithContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.nameField = new EditBox(this.font, leftPos + 91, topPos + 28, 82, 12, new TextComponent(""));
        this.nameField.setCanLoseFocus(false);
        this.nameField.setTextColor(-1);
        this.nameField.setTextColorUneditable(-1);
        this.nameField.setBordered(false);
        this.nameField.setMaxLength(35);
        this.nameField.setResponder(this::onTextChange);
        this.menu.addSlotListener(this);
        this.addRenderableWidget(nameField);
        this.setInitialFocus(nameField);
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        String s = this.nameField.getValue();
        super.resize(mc, width, height);
        this.nameField.setValue(s);
    }

    @Override
    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this);
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.player.closeContainer();
        }

        return this.nameField.keyPressed(key, scanCode, modifiers) || this.nameField.canConsumeInput() || super
                .keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.nameField.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, Textures.KEYSMITH_GUI);
        this.blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (this.nameField.canConsumeInput()) {
            this.blit(stack, leftPos + 88, topPos + 24, 0, 166, 82, 15);
        } else {
            this.blit(stack, leftPos + 88, topPos + 24, 0, 181, 82, 15);
        }
    }

    private void onTextChange(String newText) {
        boolean flag1 = newText.equals(I18n.get(Reference.Items.KEY.getDescriptionId()));
        boolean flag2 = StringUtils.isBlank(newText);
        if (flag1 || flag2) { newText = ""; }
        menu.setOutputName(newText);
        NetworkHandler.CHANNEL.sendToServer(new TextFieldChangePacket(newText));
    }

//    @Override
//    public void refreshContainer(AbstractContainerMenu container, NonNullList<ItemStack> itemsList) {
//        this.slotChanged(container, 0, container.getSlot(0).getItem());
//        this.slotChanged(container, 1, container.getSlot(1).getItem());
//        this.slotChanged(container, 2, container.getSlot(2).getItem());
//    }

    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int slotInd, ItemStack stack) {
        if (slotInd == 0) {
            if (stack.isEmpty() && isEnabledText) {
                isEnabledText = false;
                nameField.setValue("");
            } else if (!stack.isEmpty() && !isEnabledText) {
                isEnabledText = true;
                nameField.setValue(I18n.get(Reference.Items.KEY.getDescriptionId()));
            }
            this.minecraft.submitAsync(() -> this.nameField.setEditable(isEnabledText));
        } else if (slotInd == 1) {
            if (!stack.isEmpty() && isEnabledText) { nameField.setValue(stack.getHoverName().getString()); }
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerIn, int varToUpdate, int newValue) {}
}
