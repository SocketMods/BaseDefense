package tk.sciwhiz12.basedefense.item.lock;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new SerializableCapabilityProvider<>(CodedLock::new, CONTAINS_CODE, CODE_HOLDER, LOCK);
    }

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, CODE_HOLDER);
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        ItemHelper.readItemShareTag(stack, nbt, CODE_HOLDER);
    }
}
