package tk.sciwhiz12.basedefense.item.lock;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tk.sciwhiz12.basedefense.capabilities.CodedLock;
import tk.sciwhiz12.basedefense.capabilities.SerializableCapabilityProvider;
import tk.sciwhiz12.basedefense.util.ItemHelper;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.*;

public class CodedLockCoreItem extends AbstractLockCoreItem {
    public CodedLockCoreItem(Properties props) {
        super(props);
    }

    public CodedLockCoreItem() {
        super(new Properties().durability(0));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new SerializableCapabilityProvider<>(CodedLock::new, CONTAINS_CODE, CODE_HOLDER, LOCK);
    }

    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, ItemHelper.CapabilitySerializer.CODED_LOCK);
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundTag nbt) {
        ItemHelper.readItemShareTag(stack, nbt, ItemHelper.CapabilitySerializer.CODED_LOCK);
    }
}
