package sciwhiz12.basedefense.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import sciwhiz12.basedefense.Reference;
import sciwhiz12.basedefense.Reference.Containers;
import sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;
import sciwhiz12.basedefense.util.ContainerHelper;

public class PortableSafeContainer extends Container {
    private final IItemHandler inventory;
    private final IWorldPosCallable worldPos;

    public PortableSafeContainer(int windowId, PlayerInventory playerInv) {
        this(windowId, playerInv, IWorldPosCallable.DUMMY, new ItemStackHandler(18));
    }

    public PortableSafeContainer(int windowId, PlayerInventory playerInv, IWorldPosCallable worldPosIn, IItemHandler inv) {
        super(Containers.PORTABLE_SAFE, windowId);
        this.worldPos = worldPosIn;
        this.inventory = inv;
        worldPos.consume((world, pos) -> {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof PortableSafeTileEntity) {
                ((PortableSafeTileEntity) te).openInventory(playerInv.player);
            }
        });

        for (int k = 0; k < 2; ++k) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new SlotItemHandler(inv, l + k * 9, 8 + l * 18, 18 + k * 18));
            }
        }

        ContainerHelper.layoutPlayerInventorySlots(this::addSlot, playerInv, 8, 68);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(worldPos, playerIn, Reference.Blocks.PORTABLE_SAFE);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.inventory.getSlots()) {
                if (!this.mergeItemStack(itemstack1, this.inventory.getSlots(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.inventory.getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        worldPos.consume((world, pos) -> {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof PortableSafeTileEntity) {
                ((PortableSafeTileEntity) te).closeInventory(playerIn);
            }
        });
    }

    public IWorldPosCallable getWorldPos() {
        return this.worldPos;
    }
}
