package sciwhiz12.basedefense.item.lock;

import net.minecraft.item.Item;
import sciwhiz12.basedefense.api.lock.LockContext;

public class LockCoreItem extends LockBaseItem {
    public LockCoreItem() {
        super(new Item.Properties().maxDamage(0));
    }
    
    @Override
    public boolean onUnlock(LockContext ctx) {
        return true;
    }
}
