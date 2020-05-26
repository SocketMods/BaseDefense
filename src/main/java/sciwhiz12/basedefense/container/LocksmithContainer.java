package sciwhiz12.basedefense.container;

import static sciwhiz12.basedefense.init.ModTextures.ATLAS_BLOCKS_TEXTURE;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import sciwhiz12.basedefense.LockingUtil;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.init.ModContainers;
import sciwhiz12.basedefense.init.ModItems;
import sciwhiz12.basedefense.init.ModTextures;

public class LocksmithContainer extends Container {
    private final IInventory outputSlot = new CraftResultInventory() {
        public void markDirty() {
            super.markDirty();
            LocksmithContainer.this.onCraftMatrixChanged(this);
        }
    };
    private final IInventory inputSlots = new Inventory(7) {
        public void markDirty() {
            super.markDirty();
            LocksmithContainer.this.onCraftMatrixChanged(this);
        }
    };
    private final IInventory testingSlots = new Inventory(2) {
        public void markDirty() {
            super.markDirty();
            LocksmithContainer.this.onCraftMatrixChanged(this);
        }
    };
    private final InvWrapper playerInv;
    private final IWorldPosCallable worldPos;
    public final IntReferenceHolder testingState = IntReferenceHolder.single();

    public LocksmithContainer(int windowId, PlayerInventory playerInv) {
        this(windowId, playerInv, IWorldPosCallable.DUMMY);
    }

    public LocksmithContainer(int windowId, PlayerInventory playerInv, IWorldPosCallable worldPos) {
        super(ModContainers.LOCKSMITH_CONTAINER.get(), windowId);
        this.playerInv = new InvWrapper(playerInv);
        this.worldPos = worldPos;
        this.trackInt(this.testingState);
        this.testingState.set(0);

        this.addSlot(new Slot(this.inputSlots, 0, 80, 50) {
            public boolean isItemValid(ItemStack stack) {
                return Tags.Items.INGOTS_IRON.contains(stack.getItem());
            }
        }.setBackground(ATLAS_BLOCKS_TEXTURE, ModTextures.SLOT_INGOT_OUTLINE));

        for (int i = 1; i < 4; i++) {
            this.addSlot(new Slot(this.inputSlots, i, 13 + ((i - 1) * 18), 21) {
                public boolean isItemValid(ItemStack stack) {
                    return stack.getItem() == ModItems.KEY.get();
                }
            }.setBackground(ATLAS_BLOCKS_TEXTURE, ModTextures.SLOT_KEY));
        }
        for (int i = 4; i < 7; i++) {
            this.addSlot(new Slot(this.inputSlots, i, 13 + ((i - 4) * 18), 50) {
                public boolean isItemValid(ItemStack stack) {
                    return stack.getItem() == ModItems.KEY.get();
                }
            }.setBackground(ATLAS_BLOCKS_TEXTURE, ModTextures.SLOT_KEY));
        }

        this.addSlot(new Slot(this.outputSlot, 0, 80, 13) {
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            public boolean canTakeStack(PlayerEntity player) {
                return this.getHasStack();
            }

            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                LocksmithContainer.this.inputSlots.decrStackSize(0, 1);
                return stack;
            }
        }.setBackground(ATLAS_BLOCKS_TEXTURE, ModTextures.SLOT_LOCK_CORE));

        this.addSlot(new Slot(this.testingSlots, 0, 135, 19) {
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.KEY.get();
            }
        }.setBackground(ATLAS_BLOCKS_TEXTURE, ModTextures.SLOT_KEY));

        this.addSlot(new Slot(this.testingSlots, 1, 135, 40) {
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.LOCK_CORE.get();
            }
        });

        layoutPlayerInventorySlots(8, 84);
    }

    public void onCraftMatrixChanged(IInventory inv) {
        super.onCraftMatrixChanged(inv);
        if (inv == this.inputSlots) { this.updateOutputs(); }
        if (inv == this.testingSlots) { this.updateTestingState(); }
    }

    private void updateOutputs() {
        ItemStack blank = this.inputSlots.getStackInSlot(0);
        if (blank.isEmpty()) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
        } else {
            ItemStack out = new ItemStack(ModItems.LOCK_CORE.get(), 1);
            int keys = 0;
            for (int i = 1; i < 7; i++) {
                ItemStack key = this.inputSlots.getStackInSlot(i);
                if (!key.isEmpty()) {
                    LockingUtil.addUnlockID(out, LockingUtil.getKeyID(key));
                    keys++;
                }
            }
            if (keys == 0) { out = ItemStack.EMPTY; }
            this.outputSlot.setInventorySlotContents(0, out);
        }

        this.detectAndSendChanges();
    }

    private void updateTestingState() {
        int flag = 0;
        ItemStack key = this.testingSlots.getStackInSlot(0);
        ItemStack lock = this.testingSlots.getStackInSlot(1);
        if (!key.isEmpty() && !lock.isEmpty()) {
            System.out.println(LockingUtil.hasUnlockID(lock, key));
            if (LockingUtil.hasUnlockID(lock, key)) flag = 1;
        }
        this.testingState.set(flag);
        this.detectAndSendChanges();
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(worldPos, player, ModBlocks.LOCKSMITH_BLOCK.get());
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
        this.worldPos.consume((world, pos) -> {
            this.clearContainer(playerIn, world, this.inputSlots);
            this.clearContainer(playerIn, world, this.testingSlots);
        });
    }

    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            if (index == 7) {
                if (!this.mergeItemStack(slotStack, 10, 46, true)) { return ItemStack.EMPTY; }
            } else if (index > 6) {
                if (index >= 10 && index < 46 && !this.mergeItemStack(slotStack, 0, 7, false)) {
                    return ItemStack.EMPTY;
                }
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
}
