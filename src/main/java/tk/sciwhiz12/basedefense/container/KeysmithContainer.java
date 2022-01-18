package tk.sciwhiz12.basedefense.container;

import it.unimi.dsi.fastutil.longs.LongLists;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import tk.sciwhiz12.basedefense.ClientReference.Textures;
import tk.sciwhiz12.basedefense.Reference.Items;
import tk.sciwhiz12.basedefense.api.capablities.ICodeHolder;
import tk.sciwhiz12.basedefense.item.IColorable;
import tk.sciwhiz12.basedefense.util.ContainerHelper;

import java.util.Random;

import static tk.sciwhiz12.basedefense.Reference.Blocks;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static tk.sciwhiz12.basedefense.Reference.Containers;

import tk.sciwhiz12.basedefense.Reference.Blocks;
import tk.sciwhiz12.basedefense.Reference.Containers;

public class KeysmithContainer extends AbstractContainerMenu {
    private static final Random RANDOM = new Random();
    private final Container outputSlot = new ResultContainer() {
        @Override
        public void setChanged() {
            super.setChanged();
            KeysmithContainer.this.onContentsChange();
        }
    };
    private final Container inputSlots = new SimpleContainer(7) {
        @Override
        public void setChanged() {
            super.setChanged();
            KeysmithContainer.this.onContentsChange();
        }
    };
    private final ContainerLevelAccess worldPos;
    private String customName = null;

    public KeysmithContainer(int windowId, Inventory playerInv) {
        this(windowId, playerInv, ContainerLevelAccess.NULL);
    }

    public KeysmithContainer(int windowId, Inventory playerInv, ContainerLevelAccess worldPos) {
        super(Containers.KEYSMITH_TABLE, windowId);
        this.worldPos = worldPos;

        this.addSlot(new Slot(this.inputSlots, 0, 14, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.BLANK_KEY;
            }
        }.setBackground(Textures.ATLAS_BLOCKS_TEXTURE, Textures.SLOT_BLANK_KEY));
        this.addSlot(new Slot(this.inputSlots, 1, 31, 46) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.KEY;
            }
        }.setBackground(Textures.ATLAS_BLOCKS_TEXTURE, Textures.SLOT_KEY));
        this.addSlot(new Slot(this.outputSlot, 0, 64, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                KeysmithContainer.this.inputSlots.removeItem(0, 1);
                if (KeysmithContainer.this.inputSlots.getItem(0).isEmpty()) {
                    KeysmithContainer.this.setOutputName(null);
                }
            }
        }.setBackground(Textures.ATLAS_BLOCKS_TEXTURE, Textures.SLOT_KEY));
        ContainerHelper.layoutPlayerInventorySlots(this::addSlot, playerInv, 8, 84);
    }

    public void onContentsChange() {
        ItemStack blank = this.inputSlots.getItem(0);
        ItemStack dupl = this.inputSlots.getItem(1);
        ItemStack out = blank.isEmpty() ? ItemStack.EMPTY : new ItemStack(Items.KEY, 1);
        if (blank.isEmpty()) {
            this.customName = null;
        } else {
            out.getCapability(CODE_HOLDER).ifPresent(outCode -> outCode.setCodes(
                    dupl.getCapability(CODE_HOLDER).filter(holder -> holder.getCodes().size() > 0).map(ICodeHolder::getCodes)
                            .orElseGet(() -> LongLists.singleton(RANDOM.nextLong()))));
            IColorable.copyColors(dupl, out);
            if (!StringUtils.isBlank(this.customName)) {
                out.setHoverName(new TextComponent(this.customName));
            } else if (out.hasCustomHoverName()) { out.resetHoverName(); }
        }
        this.outputSlot.setItem(0, out);
        this.broadcastChanges();
    }

    public void setOutputName(String newName) {
        this.customName = newName;
        this.onContentsChange();
    }

    public String getOutputName() {
        return this.customName;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(worldPos, player, Blocks.KEYSMITH_TABLE);
    }

    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.worldPos.execute((world, pos) -> this.clearContainer(playerIn, this.inputSlots));
    }

    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            if (index == 2) {
                if (!this.moveItemStackTo(slotStack, 3, 39, true)) { return ItemStack.EMPTY; }
            } else if (index != 0 && index != 1) {
                if (index < 39 && !this.moveItemStackTo(slotStack, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 3, 39, false)) { return ItemStack.EMPTY; }

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
