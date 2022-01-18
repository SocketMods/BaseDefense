package tk.sciwhiz12.basedefense.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.Reference;
import tk.sciwhiz12.basedefense.api.ITooltipInfo;

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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (hasLockStack(stack)) {
            tooltip.add(new TranslatableComponent("tooltip.basedefense.locked_block_item.has_lock")
                .withStyle(ChatFormatting.GRAY));
            ItemStack lockStack = getLockStack(stack);
            lockStack.getCapability(Reference.Capabilities.LOCK).filter(ITooltipInfo.class::isInstance)
                .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        } else {
            tooltip.add(new TranslatableComponent("tooltip.basedefense.locked_block_item.no_lock")
                .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        ItemStack lock = getLockStack(stack);
        if (hasLockStack(stack) && lock.hasCustomHoverName()) {
            return lock.getHoverName().copy().withStyle(ChatFormatting.ITALIC);
        }
        return super.getName(stack);
    }

    public void setLockStack(ItemStack stack, ItemStack lockStack) {
        checkNotNull(stack);
        checkNotNull(lockStack);
        stack.addTagElement(TAG_LOCK_ITEM, lockStack.save(new CompoundTag()));
    }

    public ItemStack getLockStack(ItemStack stack) {
        checkNotNull(stack);
        @Nullable CompoundTag nbt = stack.getTagElement(TAG_LOCK_ITEM);
        if (nbt != null) {
            return ItemStack.of(nbt);
        }
        return ItemStack.EMPTY;
    }

    public boolean hasLockStack(ItemStack stack) {
        return stack.hasTag() && stack.getTagElement(TAG_LOCK_ITEM) != null;
    }
}
