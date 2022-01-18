package sciwhiz12.basedefense.container;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
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
import sciwhiz12.basedefense.ClientReference.Textures;
import sciwhiz12.basedefense.Reference;
import sciwhiz12.basedefense.Reference.Blocks;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.util.ContainerHelper;
import sciwhiz12.basedefense.util.UnlockHelper;

import java.util.concurrent.atomic.AtomicReference;

import static sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;

public class LocksmithContainer extends Container {
    private final IInventory outputSlot = new CraftResultInventory() {
        public void setChanged() {
            super.setChanged();
            LocksmithContainer.this.slotsChanged(this);
        }
    };
    private final IInventory inputSlots = new Inventory(7) {
        public void setChanged() {
            super.setChanged();
            LocksmithContainer.this.slotsChanged(this);
        }
    };
    private final IInventory testingSlots = new Inventory(2) {
        public void setChanged() {
            super.setChanged();
            LocksmithContainer.this.slotsChanged(this);
        }
    };
    private final IWorldPosCallable worldPos;
    public final IntReferenceHolder testingState = IntReferenceHolder.standalone();

    public LocksmithContainer(int windowId, PlayerInventory playerInv) {
        this(windowId, playerInv, IWorldPosCallable.NULL);
    }

    public LocksmithContainer(int windowId, PlayerInventory playerInv, IWorldPosCallable worldPos) {
        super(Reference.Containers.LOCKSMITH_TABLE, windowId);
        this.worldPos = worldPos;
        this.addDataSlot(this.testingState);
        this.testingState.set(0);

        this.addSlot(new Slot(this.inputSlots, 0, 80, 50) {
            public boolean mayPlace(ItemStack stack) {
                return Tags.Items.INGOTS_IRON.contains(stack.getItem());
            }
        }.setBackground(Textures.ATLAS_BLOCKS_TEXTURE, Textures.SLOT_INGOT_OUTLINE));

        for (int i = 1; i < 4; i++) {
            this.addSlot(new Slot(this.inputSlots, i, 13 + ((i - 1) * 18), 21) {
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() == Reference.Items.KEY;
                }
            }.setBackground(Textures.ATLAS_BLOCKS_TEXTURE, Textures.SLOT_KEY));
        }
        for (int i = 4; i < 7; i++) {
            this.addSlot(new Slot(this.inputSlots, i, 13 + ((i - 4) * 18), 50) {
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() == Reference.Items.KEY;
                }
            }.setBackground(Textures.ATLAS_BLOCKS_TEXTURE, Textures.SLOT_KEY));
        }

        this.addSlot(new Slot(this.outputSlot, 0, 80, 13) {
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                LocksmithContainer.this.inputSlots.removeItem(0, 1);
                return stack;
            }
        }.setBackground(Textures.ATLAS_BLOCKS_TEXTURE, Textures.SLOT_LOCK_CORE));

        this.addSlot(new Slot(this.testingSlots, 0, 135, 19) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Reference.Items.KEY;
            }
        }.setBackground(Textures.ATLAS_BLOCKS_TEXTURE, Textures.SLOT_KEY));

        this.addSlot(new Slot(this.testingSlots, 1, 135, 40) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Reference.Items.LOCK_CORE;
            }
        });

        ContainerHelper.layoutPlayerInventorySlots(this::addSlot, playerInv, 8, 84);
    }

    public void slotsChanged(IInventory inv) {
        super.slotsChanged(inv);
        if (inv == this.inputSlots || inv == this.outputSlot) { this.updateOutputs(); }
        if (inv == this.testingSlots) { this.updateTestingState(); }
    }

    private void updateOutputs() {
        ItemStack blank = this.inputSlots.getItem(0);
        if (blank.isEmpty()) {
            this.outputSlot.setItem(0, ItemStack.EMPTY);
        } else {
            ItemStack out = ItemStack.EMPTY;

            final LongList keyCodes = new LongArrayList();
            AtomicReference<ItemStack> lastKeyRef = new AtomicReference<>(ItemStack.EMPTY);
            for (int i = 1; i < 7; i++) {
                ItemStack keyStack = this.inputSlots.getItem(i);
                keyStack.getCapability(CODE_HOLDER).ifPresent(holder -> {
                    keyCodes.addAll(holder.getCodes());
                    lastKeyRef.set(keyStack);
                });
            }

            if (!keyCodes.isEmpty()) {
                out = new ItemStack(Reference.Items.LOCK_CORE, 1);
                ItemStack lastKey = lastKeyRef.get();
                out.getCapability(CODE_HOLDER).ifPresent(holder -> { for (long code : keyCodes) { holder.addCode(code); } });
                IColorable.copyColors(lastKey, out);
                if (lastKey.hasCustomHoverName()) { out.setHoverName(lastKey.getHoverName()); }
            }
            this.outputSlot.setItem(0, out);
        }

        this.broadcastChanges();
    }

    private void updateTestingState() {
        int flag = 0;
        ItemStack keyStack = this.testingSlots.getItem(0);
        ItemStack lockStack = this.testingSlots.getItem(1);
        if (!keyStack.isEmpty() && !lockStack.isEmpty()) {
            if (UnlockHelper.checkUnlock(keyStack, lockStack, IWorldPosCallable.NULL, null, false)) { flag = 1; }
        }
        this.testingState.set(flag);
        this.broadcastChanges();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(worldPos, player, Blocks.LOCKSMITH_TABLE);
    }

    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        this.worldPos.execute((world, pos) -> {
            this.clearContainer(playerIn, world, this.inputSlots);
            this.clearContainer(playerIn, world, this.testingSlots);
        });
    }

    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            if (index == 7) {
                if (!this.moveItemStackTo(slotStack, 10, 46, true)) { return ItemStack.EMPTY; }
            } else if (index > 6) {
                if (index >= 10 && index < 47 && !this.moveItemStackTo(slotStack, 0, 8, false)) { return ItemStack.EMPTY; }
            } else if (!this.moveItemStackTo(slotStack, 10, 47, false)) { return ItemStack.EMPTY; }

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
