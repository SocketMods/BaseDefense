package sciwhiz12.basedefense.item.lock;

import net.minecraft.item.Item;
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.LockContext;

public class KeyItem extends Item implements IKey {
    public KeyItem() {
        super(new Item.Properties().maxDamage(0));
    }

    @Override
    public boolean canUnlock(LockContext context) {
        return false;
    }

    @Override
    public void unlock(LockContext context) {
    }
}
