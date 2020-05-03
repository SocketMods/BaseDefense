package sciwhiz12.basedefense.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import sciwhiz12.basedefense.BDBlocks;
import sciwhiz12.basedefense.BDItems;
import sciwhiz12.basedefense.LockingUtil;

public class KeysmithContainer extends Container {
    private final IInventory outputSlot = new CraftResultInventory();
    private final IInventory inputSlots = new Inventory(2) {
        public void markDirty() {
            super.markDirty();
            KeysmithContainer.this.onCraftMatrixChanged(this);
        }
    };
    private final InvWrapper playerInv;
    private final IWorldPosCallable worldPos;

    public KeysmithContainer(int windowId, PlayerInventory playerInv) {
        this(windowId, playerInv, IWorldPosCallable.DUMMY);
    }

    public KeysmithContainer(int windowId, PlayerInventory playerInv, IWorldPosCallable worldPos) {
        super(BDBlocks.KEYSMITH_CONTAINER.get(), windowId);
        this.playerInv = new InvWrapper(playerInv);
        this.worldPos = worldPos;

        this.addSlot(new Slot(this.inputSlots, 0, 14, 24) {
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == BDItems.BLANK_KEY.get();
            }
        });
        this.addSlot(new Slot(this.inputSlots, 1, 31, 46) {
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == BDItems.KEY.get();
            }
        });
        this.addSlot(new Slot(this.outputSlot, 2, 64, 24) {
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            public boolean canTakeStack(PlayerEntity player) {
                return this.getHasStack();
            }

            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                KeysmithContainer.this.inputSlots.decrStackSize(0, 1);
                return stack;
            }
        });

        layoutPlayerInventorySlots(8, 84);
    }

    public void onCraftMatrixChanged(IInventory inv) {
        super.onCraftMatrixChanged(inv);
        if (inv == this.inputSlots) { this.updateOutputs(); }
    }

    private void updateOutputs() {
        ItemStack blank = this.inputSlots.getStackInSlot(0);
        ItemStack dupl = this.inputSlots.getStackInSlot(1);
        if (blank.isEmpty()) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
        } else {
            ItemStack out = new ItemStack(BDItems.KEY.get(), 1);
            if (!dupl.isEmpty()) {
                out.setTagInfo(LockingUtil.NBT_UUID, LongNBT.valueOf(LockingUtil.getKeyID(dupl)));
            }
            LockingUtil.getKeyID(out);
            this.outputSlot.setInventorySlotContents(0, out);
        }
        this.detectAndSendChanges();
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(worldPos, player, BDBlocks.KEYSMITH_BLOCK.get());
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx,
            int verAmount, int dy) {
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

    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.worldPos.consume(
                (world, pos) -> { this.clearContainer(playerIn, world, this.inputSlots); }
        );
    }

    // TODO: Verify this code actually works
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) { return ItemStack.EMPTY; }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0 && index != 1) {
                if (index >= 3 && index < 39 && !this.mergeItemStack(itemstack1, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) { return ItemStack.EMPTY; }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) { return ItemStack.EMPTY; }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
