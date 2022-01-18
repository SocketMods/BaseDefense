package tk.sciwhiz12.basedefense.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import tk.sciwhiz12.basedefense.Reference.Containers;
import tk.sciwhiz12.basedefense.item.key.KeyringItem;
import tk.sciwhiz12.basedefense.util.ContainerHelper;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class KeyringContainer extends AbstractContainerMenu {
    private final Inventory playerInv;
    private final IItemHandlerModifiable itemHandler;
    private final ItemStack stack;

    public KeyringContainer(int windowId, Inventory inv, FriendlyByteBuf data) {
        this(windowId, inv, data.readItem());
    }

    public KeyringContainer(int id, Inventory inv, ItemStack stack) {
        super(Containers.KEYRING, id);
        this.playerInv = inv;
        this.stack = stack;
        itemHandler = (IItemHandlerModifiable) stack.getCapability(ITEM_HANDLER_CAPABILITY)
            .orElseThrow(IllegalStateException::new);

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(itemHandler, i, 8 + i * 18, 18) {
                @Override
                public void setChanged() {
                    super.setChanged();
                    if (KeyringContainer.this.playerInv.player instanceof ServerPlayer) {
                        // ClientboundContainerSetSlotPacket: windowId = -2 means player inventory
                        ((ServerPlayer) KeyringContainer.this.playerInv.player).connection
                            .send(new ClientboundContainerSetSlotPacket(-2, 0, KeyringContainer.this.playerInv.selected, stack));
                    }
                }
            });
        }

        ContainerHelper.layoutPlayerInventorySlots(this::addSlot, playerInv, 8, 48);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        ItemStack heldStack = playerIn.getItemInHand(playerIn.getUsedItemHand());
        return heldStack.getItem() instanceof KeyringItem && ItemStack.tagMatches(heldStack, stack);
    }

    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            if (index > 8) {
                if (index < 45 && !this.moveItemStackTo(slotStack, 0, 9, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 9, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            slot.onTake(playerIn, slotStack);
        }

        return ItemStack.EMPTY;
    }
}
