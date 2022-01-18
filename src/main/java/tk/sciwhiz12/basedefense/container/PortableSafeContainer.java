package tk.sciwhiz12.basedefense.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.Reference;
import tk.sciwhiz12.basedefense.Reference.Containers;
import tk.sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;
import tk.sciwhiz12.basedefense.util.ContainerHelper;

public class PortableSafeContainer extends AbstractContainerMenu {
    private final IItemHandler inventory;
    private final ContainerLevelAccess worldPos;

    public PortableSafeContainer(int windowId, Inventory playerInv) {
        this(windowId, playerInv, ContainerLevelAccess.NULL, new ItemStackHandler(18));
    }

    public PortableSafeContainer(int windowId, Inventory playerInv, ContainerLevelAccess worldPosIn, IItemHandler inv) {
        super(Containers.PORTABLE_SAFE, windowId);
        this.worldPos = worldPosIn;
        this.inventory = inv;
        worldPos.execute((world, pos) -> {
            @Nullable BlockEntity te = world.getBlockEntity(pos);
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
    public boolean stillValid(Player playerIn) {
        return stillValid(worldPos, playerIn, Reference.Blocks.PORTABLE_SAFE);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.inventory.getSlots()) {
                if (!this.moveItemStackTo(itemstack1, this.inventory.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.inventory.getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        worldPos.execute((world, pos) -> {
            @Nullable BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof PortableSafeTileEntity) {
                ((PortableSafeTileEntity) te).closeInventory(playerIn);
            }
        });
    }

    public ContainerLevelAccess getWorldPos() {
        return this.worldPos;
    }
}
