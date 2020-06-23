package sciwhiz12.basedefense.item;

import static com.google.common.base.Preconditions.checkNotNull;
import static sciwhiz12.basedefense.Reference.ITEM_GROUP;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.util.ItemHelper;

public class LockedDoorBlockItem extends BlockItem {
    public LockedDoorBlockItem(LockedDoorBlock blockIn) {
        super(blockIn, new Item.Properties().group(ITEM_GROUP).maxDamage(0));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (hasLockStack(stack)) {
            tooltip.add(
                new TranslationTextComponent("tooltip.basedefense.locked_door.has_lock").applyTextStyle(TextFormatting.GRAY)
            );
            if (!flagIn.isAdvanced()) { return; }
            ItemStack lockStack = getLockStack(stack);
            ItemHelper.addCodeInformation(lockStack, tooltip);
            ItemHelper.addColorInformation(lockStack, tooltip);
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        ItemStack lock = getLockStack(stack);
        if (!lock.isEmpty()) { return lock.getDisplayName(); }
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
