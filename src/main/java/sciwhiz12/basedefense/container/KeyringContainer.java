package sciwhiz12.basedefense.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import sciwhiz12.basedefense.init.ModContainers;
import sciwhiz12.basedefense.item.key.KeyringItem;
import sciwhiz12.basedefense.net.NetworkHandler;
import sciwhiz12.basedefense.net.UpdatePlayerInvSlotPacket;

public class KeyringContainer extends Container {
    private final InvWrapper playerInv;
    private final IItemHandlerModifiable itemHandler;
    private final ItemStack stack;

    public KeyringContainer(int id, PlayerInventory inv) {
        this(id, inv, ItemStack.EMPTY);
    }

    public KeyringContainer(int id, PlayerInventory inv, ItemStack stack) {
        super(ModContainers.KEYRING, id);
        this.playerInv = new InvWrapper(inv);
        itemHandler = (IItemHandlerModifiable) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(
            new ItemStackHandler(9)
        );
        this.stack = stack;

        addSlotRange(itemHandler, 0, 8, 18, 9, 18);
        layoutPlayerInventorySlots(8, 48);
    }

    public void onContainerClosed(PlayerEntity playerIn) {
        if (playerIn instanceof ServerPlayerEntity) {
            for (int i = 0; i < playerInv.getSlots(); i++) {
                if (playerInv.getStackInSlot(i) == stack) {
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerIn),
                        new UpdatePlayerInvSlotPacket(i, stack));
                    break;
                }
            }
        }
        super.onContainerClosed(playerIn);
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

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        addSlotBox(playerInv, 9, leftCol, topRow, 9, 18, 3, 18);
        topRow += 58;
        addSlotRange(playerInv, 0, leftCol, topRow, 9, 18);
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
