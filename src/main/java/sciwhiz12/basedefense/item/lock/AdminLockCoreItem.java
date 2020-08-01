package sciwhiz12.basedefense.item.lock;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import sciwhiz12.basedefense.capabilities.AdminKeyLock;
import sciwhiz12.basedefense.capabilities.GenericCapabilityProvider;

import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class AdminLockCoreItem extends AbstractLockCoreItem {
    public AdminLockCoreItem() {
        super(new Item.Properties().maxDamage(0).rarity(Rarity.EPIC).group(ITEM_GROUP));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new GenericCapabilityProvider<>(AdminKeyLock::new, LOCK);
    }
}
