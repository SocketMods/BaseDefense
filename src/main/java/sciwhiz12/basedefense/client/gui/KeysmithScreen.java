package sciwhiz12.basedefense.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;
import sciwhiz12.basedefense.ClientReference.Textures;
import sciwhiz12.basedefense.Reference;
import sciwhiz12.basedefense.container.KeysmithContainer;
import sciwhiz12.basedefense.net.NetworkHandler;
import sciwhiz12.basedefense.net.TextFieldChangePacket;

public class KeysmithScreen extends ContainerScreen<KeysmithContainer> implements IContainerListener {
    private TextFieldWidget nameField;
    private boolean isEnabledText = false;

    public KeysmithScreen(KeysmithContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.nameField = new TextFieldWidget(this.font, leftPos + 91, topPos + 28, 82, 12, new StringTextComponent(""));
        this.nameField.setCanLoseFocus(false);
        this.nameField.setTextColor(-1);
        this.nameField.setTextColorUneditable(-1);
        this.nameField.setBordered(false);
        this.nameField.setMaxLength(35);
        this.nameField.setResponder(this::onTextChange);
        this.menu.addSlotListener(this);
        this.children.add(nameField);
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.nameField.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(Textures.KEYSMITH_GUI);
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

    @Override
    public void refreshContainer(Container container, NonNullList<ItemStack> itemsList) {
        this.slotChanged(container, 0, container.getSlot(0).getItem());
        this.slotChanged(container, 1, container.getSlot(1).getItem());
        this.slotChanged(container, 2, container.getSlot(2).getItem());
    }

    @Override
    public void slotChanged(Container containerToSend, int slotInd, ItemStack stack) {
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
    public void setContainerData(Container containerIn, int varToUpdate, int newValue) {}
}
