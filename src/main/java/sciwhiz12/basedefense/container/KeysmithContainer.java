package sciwhiz12.basedefense.container;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import sciwhiz12.basedefense.LockingUtil;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.init.ModContainers;
import sciwhiz12.basedefense.init.ModItems;

public class KeysmithContainer extends Container {
    private final IInventory outputSlot = new Inventory(1);
    private final IInventory inputSlots = new Inventory(2) {
        public void markDirty() {
            super.markDirty();
            KeysmithContainer.this.onCraftMatrixChanged(this);
        }
    };
    private final InvWrapper playerInv;
    private final IWorldPosCallable worldPos;
    private String name = null;

    public static final int BLANK_INPUT_SLOT = 0;
    public static final int DUPLICATE_INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    public KeysmithContainer(int windowId, PlayerInventory playerInv) {
        this(windowId, playerInv, IWorldPosCallable.DUMMY);
    }

    public KeysmithContainer(int windowId, PlayerInventory playerInv, IWorldPosCallable worldPos) {
        super(ModContainers.KEYSMITH_CONTAINER.get(), windowId);
        this.playerInv = new InvWrapper(playerInv);
        this.worldPos = worldPos;

        this.addSlot(new Slot(this.inputSlots, 0, 14, 24) {
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.BLANK_KEY.get();
            }
        });
        this.addSlot(new Slot(this.inputSlots, 1, 31, 46) {
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.KEY.get();
            }
        });
        this.addSlot(new Slot(this.outputSlot, 0, 64, 24) {
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                KeysmithContainer.this.inputSlots.decrStackSize(0, 1);
                //KeysmithContainer.this.changeOutputName(null);
                return stack;
            }
        });
        layoutPlayerInventorySlots(8, 84);
    }

    public void onCraftMatrixChanged(IInventory inv) {
        super.onCraftMatrixChanged(inv);
        if (inv == this.inputSlots) { this.updateOutputs(); }
        this.detectAndSendChanges();
    }

    private void updateOutputs() {
        ItemStack blank = this.inputSlots.getStackInSlot(0);
        ItemStack dupl = this.inputSlots.getStackInSlot(1);
        if (blank.isEmpty()) {
            this.name = null;
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
        } else {
            ItemStack out = new ItemStack(ModItems.KEY.get(), 1);
            if (!dupl.isEmpty()) {
                out.setTagInfo(LockingUtil.NBT_UUID, LongNBT.valueOf(LockingUtil.getKeyID(dupl)));
            }
            LockingUtil.getKeyID(out);
            this.outputSlot.setInventorySlotContents(0, out);
            if (name == null) { this.name = out.getDisplayName().getString(); }
        }
    }

    public void changeOutputName(String text) {
        this.name = text;
        if (this.getSlot(2).getHasStack()) {
            ItemStack itemstack = this.getSlot(2).getStack();
            if (StringUtils.isBlank(text)) {
                itemstack.clearCustomName();
            } else {
                StringTextComponent displayName = new StringTextComponent(this.name);
                displayName.getStyle().setItalic(false);
                itemstack.setDisplayName(displayName);
            }
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(worldPos, player, ModBlocks.KEYSMITH_BLOCK.get());
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

    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            if (index == 2) {
                if (!this.mergeItemStack(slotStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index != 0 && index != 1) {
                if (index >= 3 && index < 39 && !this.mergeItemStack(
                        slotStack, 0, 1, false
                )) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

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
