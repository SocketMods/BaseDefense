package sciwhiz12.basedefense.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import sciwhiz12.basedefense.init.ModContainers;
import sciwhiz12.basedefense.item.key.KeyringItem;
import sciwhiz12.basedefense.item.key.KeyringItem.KeyringProvider;
import sciwhiz12.basedefense.util.ContainerHelper;

public class KeyringContainer extends Container {
    private final PlayerInventory playerInv;
    private final IItemHandlerModifiable itemHandler;

    public KeyringContainer(int id, PlayerInventory inv) {
        this(id, inv, ItemStack.EMPTY);
    }

    public KeyringContainer(int id, PlayerInventory inv, ItemStack stack) {
        super(ModContainers.KEYRING, id);
        this.playerInv = inv;
        itemHandler = (IItemHandlerModifiable) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(
            KeyringProvider::createItemHandler);

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(itemHandler, i, 8 + i * 18, 18) {
                @Override
                public void onSlotChanged() {
                    super.onSlotChanged();
                    if (KeyringContainer.this.playerInv.player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) KeyringContainer.this.playerInv.player).connection.sendPacket(
                            new SSetSlotPacket(-2, KeyringContainer.this.playerInv.currentItem, stack));
                    }
                }
            });
        }

        ContainerHelper.layoutPlayerInventorySlots(this::addSlot, playerInv, 8, 48);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return playerIn.getHeldItem(playerIn.getActiveHand()).getItem() instanceof KeyringItem;
    }

    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            if (index == 7) {
                if (!this.mergeItemStack(slotStack, 10, 46, true)) { return ItemStack.EMPTY; }
            } else if (index > 6) {
                if (index >= 10 && index < 46 && !this.mergeItemStack(slotStack, 0, 7, false)) { return ItemStack.EMPTY; }
            } else if (!this.mergeItemStack(slotStack, 10, 46, false)) { return ItemStack.EMPTY; }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(playerIn, slotStack);
        }

        return ItemStack.EMPTY;
    }

    public static class Provider implements INamedContainerProvider {
        private final ItemStack stack;

        public Provider(ItemStack stackIn) {
            this.stack = stackIn;
        }

        @Override
        public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
            return new KeyringContainer(windowId, playerInv, stack);
        }

        @Override
        public ITextComponent getDisplayName() {
            return stack.getDisplayName();
        }
    }
}
