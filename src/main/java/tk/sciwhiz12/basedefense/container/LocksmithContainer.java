package tk.sciwhiz12.basedefense.container;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import tk.sciwhiz12.basedefense.ClientReference.Textures;
import tk.sciwhiz12.basedefense.Reference;
import tk.sciwhiz12.basedefense.Reference.Blocks;
import tk.sciwhiz12.basedefense.item.IColorable;
import tk.sciwhiz12.basedefense.util.ContainerHelper;
import tk.sciwhiz12.basedefense.util.UnlockHelper;

import java.util.concurrent.atomic.AtomicReference;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;

public class LocksmithContainer extends AbstractContainerMenu {
    private final Container outputSlot = new ResultContainer() {
        public void setChanged() {
            super.setChanged();
            LocksmithContainer.this.slotsChanged(this);
        }
    };
    private final Container inputSlots = new SimpleContainer(7) {
        public void setChanged() {
            super.setChanged();
            LocksmithContainer.this.slotsChanged(this);
        }
    };
    private final Container testingSlots = new SimpleContainer(2) {
        public void setChanged() {
            super.setChanged();
            LocksmithContainer.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess worldPos;
    public final DataSlot testingState = DataSlot.standalone();

    public LocksmithContainer(int windowId, Inventory playerInv) {
        this(windowId, playerInv, ContainerLevelAccess.NULL);
    }

    public LocksmithContainer(int windowId, Inventory playerInv, ContainerLevelAccess worldPos) {
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

            public void onTake(Player player, ItemStack stack) {
                LocksmithContainer.this.inputSlots.removeItem(0, 1);
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

    public void slotsChanged(Container inv) {
        super.slotsChanged(inv);
        if (inv == this.inputSlots || inv == this.outputSlot) {
            this.updateOutputs();
        }
        if (inv == this.testingSlots) {
            this.updateTestingState();
        }
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
                out.getCapability(CODE_HOLDER).ifPresent(holder -> {
                    for (long code : keyCodes) {
                        holder.addCode(code);
                    }
                });
                IColorable.copyColors(lastKey, out);
                if (lastKey.hasCustomHoverName()) {
                    out.setHoverName(lastKey.getHoverName());
                }
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
            if (UnlockHelper.checkUnlock(keyStack, lockStack, ContainerLevelAccess.NULL, null, false)) {
                flag = 1;
            }
        }
        this.testingState.set(flag);
        this.broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(worldPos, player, Blocks.LOCKSMITH_TABLE);
    }

    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.worldPos.execute((world, pos) -> {
            this.clearContainer(playerIn, this.inputSlots);
            this.clearContainer(playerIn, this.testingSlots);
        });
    }

    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            if (index == 7) {
                if (!this.moveItemStackTo(slotStack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index > 6) {
                if (index >= 10 && index < 47 && !this.moveItemStackTo(slotStack, 0, 8, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 10, 47, false)) {
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
