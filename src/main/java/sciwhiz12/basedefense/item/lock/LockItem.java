package sciwhiz12.basedefense.item.lock;

import net.minecraft.item.Item;
import sciwhiz12.basedefense.api.lock.ILock;
import sciwhiz12.basedefense.api.lock.LockContext;

public class LockItem extends Item implements ILock {
    public LockItem() {
        super(new Item.Properties().maxDamage(0));
    }

    @Override
    public void onUnlock(LockContext context) {}
}
