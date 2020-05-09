package sciwhiz12.basedefense.api.lock;

import net.minecraft.item.ItemStack;

public interface ILockable {
    public ItemStack getLock();

    public boolean hasLock();

    public void setLock(ItemStack stack);

    public boolean onUnlock(LockContext context);
}
