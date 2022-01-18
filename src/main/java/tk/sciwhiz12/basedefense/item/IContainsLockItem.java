package tk.sciwhiz12.basedefense.item;

import net.minecraft.item.ItemStack;

public interface IContainsLockItem {
    void setLockStack(ItemStack stack, ItemStack lock);

    ItemStack getLockStack(ItemStack stack);
}
