package sciwhiz12.basedefense.container;

import static sciwhiz12.basedefense.init.ModTextures.ATLAS_BLOCKS_TEXTURE;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import sciwhiz12.basedefense.capabilities.CodedKey;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.init.ModCapabilities;
import sciwhiz12.basedefense.init.ModContainers;
import sciwhiz12.basedefense.init.ModItems;
import sciwhiz12.basedefense.init.ModTextures;
import sciwhiz12.basedefense.item.IColorable;

public class KeysmithContainer extends Container {
    private static final Random RANDOM = new Random();
    private final IInventory outputSlot = new CraftResultInventory() {
        @Override
        public void markDirty() {
            super.markDirty();
            KeysmithContainer.this.onContentsChange();
        }
    };
    private final IInventory inputSlots = new Inventory(7) {
        @Override
        public void markDirty() {
            super.markDirty();
            KeysmithContainer.this.onContentsChange();
        }
    };
    private final InvWrapper playerInv;
    private final IWorldPosCallable worldPos;
    private String customName = null;

    public KeysmithContainer(int windowId, PlayerInventory playerInv) {
        this(windowId, playerInv, IWorldPosCallable.DUMMY);
    }

    public KeysmithContainer(int windowId, PlayerInventory playerInv, IWorldPosCallable worldPos) {
        super(ModContainers.KEYSMITH_TABLE, windowId);
        this.playerInv = new InvWrapper(playerInv);
        this.worldPos = worldPos;

        this.addSlot(new Slot(this.inputSlots, 0, 14, 24) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.BLANK_KEY;
            }
        }.setBackground(ATLAS_BLOCKS_TEXTURE, ModTextures.SLOT_BLANK_KEY));
        this.addSlot(new Slot(this.inputSlots, 1, 31, 46) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.KEY;
            }
        }.setBackground(ATLAS_BLOCKS_TEXTURE, ModTextures.SLOT_KEY));
        this.addSlot(new Slot(this.outputSlot, 0, 64, 24) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                stack.getCapability(ModCapabilities.KEY).ifPresent((key) -> {
                    if (key instanceof CodedKey) {
                        System.out.println(!player.world.isRemote);
                        System.out.println(((CodedKey) key).getCode());
                    }
                });
                KeysmithContainer.this.inputSlots.decrStackSize(0, 1);
                KeysmithContainer.this.setOutputName(null);
                return stack;
            }
        }.setBackground(ATLAS_BLOCKS_TEXTURE, ModTextures.SLOT_KEY));
        layoutPlayerInventorySlots(8, 84);
    }

    public void onContentsChange() {
        ItemStack blank = this.inputSlots.getStackInSlot(0);
        ItemStack dupl = this.inputSlots.getStackInSlot(1);
        ItemStack out = ItemStack.EMPTY;
        if (blank.isEmpty()) {
            this.customName = null;
        } else {
            out = new ItemStack(ModItems.KEY, 1);
            AtomicLong code = new AtomicLong(RANDOM.nextLong());
            if (!dupl.isEmpty()) {
                dupl.getCapability(ModCapabilities.KEY).ifPresent((duplKey) -> {
                    if (duplKey instanceof CodedKey) { code.set(((CodedKey) duplKey).getCode()); }
                });
                IColorable.copyColors(dupl, out);
            }
            out.getCapability(ModCapabilities.KEY).ifPresent((key) -> {
                if (key instanceof CodedKey) { ((CodedKey) key).setCode(code.get()); }
            });
            if (!StringUtils.isBlank(this.customName)) {
                out.setDisplayName(new StringTextComponent(this.customName));
            } else if (out.hasDisplayName()) { out.clearCustomName(); }
        }
        this.outputSlot.setInventorySlotContents(0, out);
        this.detectAndSendChanges();
    }

    public void setOutputName(String newName) {
        this.customName = newName;
        this.onContentsChange();
    }

    public String getOutputName() {
        return this.customName;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(worldPos, player, ModBlocks.KEYSMITH_TABLE);
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

    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.worldPos.consume((world, pos) -> { this.clearContainer(playerIn, world, this.inputSlots); });
    }

    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            if (index == 2) {
                if (!this.mergeItemStack(slotStack, 3, 39, true)) { return ItemStack.EMPTY; }
            } else if (index != 0 && index != 1) {
                if (index >= 3 && index < 39 && !this.mergeItemStack(slotStack, 0, 1, false)) { return ItemStack.EMPTY; }
            } else if (!this.mergeItemStack(slotStack, 3, 39, false)) { return ItemStack.EMPTY; }

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
