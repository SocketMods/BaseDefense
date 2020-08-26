package sciwhiz12.basedefense.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import sciwhiz12.basedefense.api.ITooltipInfo;
import sciwhiz12.basedefense.block.LockedDoorBlock;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class LockedDoorBlockItem extends BlockItem implements IContainsLockItem {
    public LockedDoorBlockItem(LockedDoorBlock blockIn) {
        super(blockIn, new Item.Properties().group(ITEM_GROUP).maxDamage(0));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (hasLockStack(stack)) {
            ItemStack lockStack = getLockStack(stack);
            tooltip.add(new TranslationTextComponent("tooltip.basedefense.locked_door.has_lock")
                    .mergeStyle(TextFormatting.GRAY));
            lockStack.getCapability(LOCK).filter(ITooltipInfo.class::isInstance)
                    .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        ItemStack lock = getLockStack(stack);
        if (hasLockStack(stack) && lock.hasDisplayName()) {
            return lock.getDisplayName().deepCopy().mergeStyle(TextFormatting.ITALIC);
        }
        return super.getDisplayName(stack);
    }

    public void setLockStack(ItemStack stack, ItemStack lockStack) {
        checkNotNull(stack);
        checkNotNull(lockStack);
        stack.setTagInfo("LockItem", lockStack.write(new CompoundNBT()));
    }

    public ItemStack getLockStack(ItemStack stack) {
        checkNotNull(stack);
        CompoundNBT nbt = stack.getChildTag("LockItem");
        if (nbt != null) { return ItemStack.read(nbt); }
        return ItemStack.EMPTY;
    }

    public boolean hasLockStack(ItemStack stack) {
        return stack.hasTag() && stack.getChildTag("LockItem") != null;
    }
}
