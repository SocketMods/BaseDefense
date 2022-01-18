package tk.sciwhiz12.basedefense.item;

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
import tk.sciwhiz12.basedefense.api.ITooltipInfo;
import tk.sciwhiz12.basedefense.Reference;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class LockedBlockItem extends BlockItem implements IContainsLockItem {
    public static final String TAG_LOCK_ITEM = "LockItem";

    public LockedBlockItem(Block blockIn, Item.Properties properties) {
        super(blockIn, properties);
    }

    public LockedBlockItem(Block blockIn) {
        this(blockIn, new Item.Properties().tab(Reference.ITEM_GROUP).durability(0));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (hasLockStack(stack)) {
            tooltip.add(new TranslationTextComponent("tooltip.basedefense.locked_block_item.has_lock")
                    .withStyle(TextFormatting.GRAY));
            ItemStack lockStack = getLockStack(stack);
            lockStack.getCapability(Reference.Capabilities.LOCK).filter(ITooltipInfo.class::isInstance)
                    .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.basedefense.locked_block_item.no_lock")
                    .withStyle(TextFormatting.GRAY));
        }
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        ItemStack lock = getLockStack(stack);
        if (hasLockStack(stack) && lock.hasCustomHoverName()) {
            return lock.getHoverName().copy().withStyle(TextFormatting.ITALIC);
        }
        return super.getName(stack);
    }

    public void setLockStack(ItemStack stack, ItemStack lockStack) {
        checkNotNull(stack);
        checkNotNull(lockStack);
        stack.addTagElement(TAG_LOCK_ITEM, lockStack.save(new CompoundNBT()));
    }

    public ItemStack getLockStack(ItemStack stack) {
        checkNotNull(stack);
        CompoundNBT nbt = stack.getTagElement(TAG_LOCK_ITEM);
        if (nbt != null) { return ItemStack.of(nbt); }
        return ItemStack.EMPTY;
    }

    public boolean hasLockStack(ItemStack stack) {
        return stack.hasTag() && stack.getTagElement(TAG_LOCK_ITEM) != null;
    }
}
