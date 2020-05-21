package sciwhiz12.basedefense.client.gui;

import org.apache.commons.lang3.StringUtils;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.container.KeysmithContainer;
import sciwhiz12.basedefense.init.ModItems;
import sciwhiz12.basedefense.net.NetworkHandler;
import sciwhiz12.basedefense.net.TextFieldChangePacket;

public class KeysmithScreen extends ContainerScreen<KeysmithContainer> implements
        IContainerListener {
    private TextFieldWidget nameField;

    public KeysmithScreen(KeysmithContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.nameField = new TextFieldWidget(
            this.font, i + 91, j + 28, 82, 12, I18n.format("container.repair")
        );
        this.nameField.setCanLoseFocus(false);
        this.nameField.changeFocus(true);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(35);
        this.nameField.setResponder(this::onTextChange);
        this.container.addListener(this);
        this.children.add(this.nameField);
        this.setFocusedDefault(this.nameField);
    }

    private final ResourceLocation KEYSMITH_GUI = new ResourceLocation(
        BaseDefense.MODID, "textures/gui/keysmith_gui.png"
    );

    @Override
    public void resize(Minecraft mc, int width, int height) {
        String s = this.nameField.getText();
        this.init(mc, width, height);
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
        if (key == 256) { this.minecraft.player.closeScreen(); }

        return !this.nameField.keyPressed(key, scanCode, modifiers) && !this.nameField.canWrite()
                ? super.keyPressed(key, scanCode, modifiers)
                : true;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
        this.nameField.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 8, 6, 4210752);
        this.font.drawString(
            this.playerInventory.getDisplayName().getFormattedText(), 8, 73, 4210752
        );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(KEYSMITH_GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
        if (this.nameField.canWrite()) {
            this.blit(relX + 88, relY + 24, 0, 166, 82, 15);
        } else {
            this.blit(relX + 88, relY + 24, 0, 181, 82, 15);
        }
    }

    private void onTextChange(String newText) {
        String s = newText;
        if (StringUtils.isEmpty(newText)) { s = ""; }
        if (!this.container.getSlot(2).getHasStack()) return;
        this.container.changeOutputName(s);
        NetworkHandler.CHANNEL.sendToServer(new TextFieldChangePacket(s));
    }

    @Override
    public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
        this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).getStack());
        this.sendSlotContents(containerToSend, 1, containerToSend.getSlot(1).getStack());
        this.sendSlotContents(containerToSend, 2, containerToSend.getSlot(2).getStack());
    }

    @Override
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
        if (slotInd == 0) {
            this.nameField.setEnabled(!stack.isEmpty());
            if (StringUtils.isEmpty(this.nameField.getText())) {
                this.nameField.setText(ModItems.KEY.get().getName().getString());
            }
        } else if (slotInd == 1) {
            this.nameField.setText(stack.isEmpty() ? "" : stack.getDisplayName().getString());
        } else if (slotInd == 2) { if (stack.isEmpty()) { this.nameField.setText(""); } }
    }

    @Override
    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {}
}
