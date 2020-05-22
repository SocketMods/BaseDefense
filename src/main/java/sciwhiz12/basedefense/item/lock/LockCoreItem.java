package sciwhiz12.basedefense.item.lock;

import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import sciwhiz12.basedefense.api.lock.LockContext;

public class LockCoreItem extends LockBaseItem {
    private static final IItemPropertyGetter COLOR_GETTER = (stack, world, livingEntity) -> {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) {
            return (float) tag.getIntArray("colors").length;
        }
        return 0.0F;
    };
    
    public LockCoreItem() {
        super(new Item.Properties().maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("colors"), COLOR_GETTER);
    }
    
    @Override
    public boolean onUnlock(LockContext ctx) {
        return true;
    }
}
