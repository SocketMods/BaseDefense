package sciwhiz12.basedefense.container;

import static sciwhiz12.basedefense.init.ModTextures.ATLAS_BLOCKS_TEXTURE;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import sciwhiz12.basedefense.LockingUtil;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.init.ModContainers;
import sciwhiz12.basedefense.init.ModItems;
import sciwhiz12.basedefense.init.ModTextures;

public class KeysmithContainer extends Container {
    private final ItemStackHandler outputSlot = new ItemStackHandler(1) {};
    private final ItemStackHandler inputSlots = new ItemStackHandler(2) {
        @Override
        public void onContentsChanged(int slot) {
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
        super(ModContainers.KEYSMITH_CONTAINER.get(), windowId);
        this.playerInv = new InvWrapper(playerInv);
        this.worldPos = worldPos;

        this.addSlot(new SlotItemHandler(this.inputSlots, 0, 14, 24) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.BLANK_KEY.get();
            }
        }.setBackground(ATLAS_BLOCKS_TEXTURE, ModTextures.SLOT_BLANK_KEY));
        this.addSlot(new SlotItemHandler(this.inputSlots, 1, 31, 46) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.KEY.get();
            }
        }.setBackground(ATLAS_BLOCKS_TEXTURE, ModTextures.SLOT_KEY));
        this.addSlot(new SlotItemHandler(this.outputSlot, 0, 64, 24) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                KeysmithContainer.this.inputSlots.extractItem(0, 1, false);
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
            out = new ItemStack(ModItems.KEY.get(), 1);
            boolean duplEmpty = dupl.isEmpty();
            if (!duplEmpty) {
                out.setTagInfo(LockingUtil.NBT_UUID, LongNBT.valueOf(LockingUtil.getKeyID(dupl)));
            } else {
                LockingUtil.getKeyID(out);
            }
            if (!StringUtils.isBlank(this.customName)) {
                out.setDisplayName(new StringTextComponent(this.customName));
            } else if (out.hasDisplayName()) { out.clearCustomName(); }
        }
        this.outputSlot.setStackInSlot(0, out);
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

    private void clearContainer(PlayerEntity player, World worldIn, ItemStackHandler inv) {
        if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).hasDisconnected()) {
            for (int j = 0; j < inv.getSlots(); ++j) {
                player.dropItem(inv.extractItem(j, inv.getSlotLimit(j), false), false);
            }

        } else {
            for (int i = 0; i < inv.getSlots(); ++i) {
                player.inventory.placeItemBackInInventory(worldIn, inv.extractItem(i, inv.getSlotLimit(i), false));
            }
        }
    }
}
