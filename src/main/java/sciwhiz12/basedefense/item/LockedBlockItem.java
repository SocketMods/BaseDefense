package sciwhiz12.basedefense.item;

import net.minecraft.block.Block;
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

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class LockedBlockItem extends BlockItem implements IContainsLockItem {
    public static final String TAG_LOCK_ITEM = "LockItem";

    public LockedBlockItem(Block blockIn, Item.Properties properties) {
        super(blockIn, properties);
    }

    public LockedBlockItem(Block blockIn) {
        this(blockIn, new Item.Properties().group(ITEM_GROUP).maxDamage(0));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (hasLockStack(stack)) {
            tooltip.add(new TranslationTextComponent("tooltip.basedefense.locked_block_item.has_lock")
                    .mergeStyle(TextFormatting.GRAY));
            ItemStack lockStack = getLockStack(stack);
            lockStack.getCapability(LOCK).filter(ITooltipInfo.class::isInstance)
                    .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.basedefense.locked_block_item.no_lock")
                    .mergeStyle(TextFormatting.GRAY));
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
        stack.setTagInfo(TAG_LOCK_ITEM, lockStack.write(new CompoundNBT()));
    }

    public ItemStack getLockStack(ItemStack stack) {
        checkNotNull(stack);
        CompoundNBT nbt = stack.getChildTag(TAG_LOCK_ITEM);
        if (nbt != null) { return ItemStack.read(nbt); }
        return ItemStack.EMPTY;
    }

    public boolean hasLockStack(ItemStack stack) {
        return stack.hasTag() && stack.getChildTag(TAG_LOCK_ITEM) != null;
    }
}
