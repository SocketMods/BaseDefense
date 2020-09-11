package sciwhiz12.basedefense.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import sciwhiz12.basedefense.Reference.Containers;
import sciwhiz12.basedefense.item.key.KeyringItem;
import sciwhiz12.basedefense.util.ContainerHelper;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class KeyringContainer extends Container {
    private final PlayerInventory playerInv;
    private final IItemHandlerModifiable itemHandler;
    private final ItemStack stack;

    public KeyringContainer(int windowId, PlayerInventory inv, PacketBuffer data) {
        this(windowId, inv, data.readItemStack());
    }

    public KeyringContainer(int id, PlayerInventory inv, ItemStack stack) {
        super(Containers.KEYRING, id);
        this.playerInv = inv;
        this.stack = stack;
        itemHandler = (IItemHandlerModifiable) stack.getCapability(ITEM_HANDLER_CAPABILITY)
                .orElseThrow(IllegalStateException::new);

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(itemHandler, i, 8 + i * 18, 18) {
                @Override
                public void onSlotChanged() {
                    super.onSlotChanged();
                    if (KeyringContainer.this.playerInv.player instanceof ServerPlayerEntity) {
                        // SSetSlotPacket: windowId = -2 means player inventory
                        ((ServerPlayerEntity) KeyringContainer.this.playerInv.player).connection
                                .sendPacket(new SSetSlotPacket(-2, KeyringContainer.this.playerInv.currentItem, stack));
                    }
                }
            });
        }

        ContainerHelper.layoutPlayerInventorySlots(this::addSlot, playerInv, 8, 48);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        ItemStack heldStack = playerIn.getHeldItem(playerIn.getActiveHand());
        return heldStack.getItem() instanceof KeyringItem && ItemStack.areItemStackTagsEqual(heldStack, stack);
    }

    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            if (index > 8) {
                if (index < 45 && !this.mergeItemStack(slotStack, 0, 9, false)) { return ItemStack.EMPTY; }
            } else if (!this.mergeItemStack(slotStack, 9, 46, false)) { return ItemStack.EMPTY; }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(playerIn, slotStack);
        }

        return ItemStack.EMPTY;
    }
}
