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
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.nameField = new TextFieldWidget(
                this.font, guiLeft + 91, guiTop + 28, 82, 12, new StringTextComponent("")
        );
        this.nameField.setCanLoseFocus(false);
        this.nameField.setFocused2(true);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(35);
        this.nameField.setResponder(this::onTextChange);
        this.container.addListener(this);
        this.children.add(nameField);
        this.setFocusedDefault(nameField);
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        String s = this.nameField.getText();
        super.resize(mc, width, height);
        this.nameField.setText(s);
    }

    @Override
    public void removed() {
        super.removed();
        this.container.removeListener(this);
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.player.closeScreen();
        }

        return this.nameField.keyPressed(key, scanCode, modifiers) || this.nameField.canWrite() || super.keyPressed(
                key, scanCode, modifiers
        );
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.nameField.render(stack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(stack, mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void func_230450_a_(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(Textures.KEYSMITH_GUI);
        this.blit(stack, guiLeft, guiTop, 0, 0, xSize, ySize);
        if (this.nameField.canWrite()) {
            this.blit(stack, guiLeft + 88, guiTop + 24, 0, 166, 82, 15);
        } else {
            this.blit(stack, guiLeft + 88, guiTop + 24, 0, 181, 82, 15);
        }
    }

    private void onTextChange(String newText) {
        boolean flag1 = newText.equals(I18n.format(Reference.Items.KEY.getTranslationKey()));
        boolean flag2 = StringUtils.isBlank(newText);
        if (flag1 || flag2) { newText = ""; }
        container.setOutputName(newText);
        NetworkHandler.CHANNEL.sendToServer(new TextFieldChangePacket(newText));
    }

    @Override
    public void sendAllContents(Container container, NonNullList<ItemStack> itemsList) {
        this.sendSlotContents(container, 0, container.getSlot(0).getStack());
        this.sendSlotContents(container, 1, container.getSlot(1).getStack());
        this.sendSlotContents(container, 2, container.getSlot(2).getStack());
    }

    @Override
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
        if (slotInd == 0) {
            if (stack.isEmpty() && isEnabledText) {
                isEnabledText = false;
                nameField.setText("");
            } else if (!stack.isEmpty() && !isEnabledText) {
                isEnabledText = true;
                nameField.setText(I18n.format(Reference.Items.KEY.getTranslationKey()));
            }
            this.minecraft.deferTask(() -> this.nameField.setEnabled(isEnabledText));
        } else if (slotInd == 1) {
            if (!stack.isEmpty() && isEnabledText) { nameField.setText(stack.getDisplayName().getString()); }
        }
    }

    @Override
    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {}
}
