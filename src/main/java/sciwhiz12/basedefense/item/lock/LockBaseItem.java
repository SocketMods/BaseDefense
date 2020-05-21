package sciwhiz12.basedefense.item.lock;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import sciwhiz12.basedefense.LockingUtil;
import sciwhiz12.basedefense.api.lock.ILock;
import sciwhiz12.basedefense.api.lock.LockContext;

public abstract class LockBaseItem extends Item implements ILock {
    public LockBaseItem(Item.Properties props) {
        super(props);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn,
            List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!flagIn.isAdvanced()) return;
        long[] ids = LockingUtil.getUnlockIDs(stack);
        if (ids.length != 0) {
            tooltip.add(
                new TranslationTextComponent("tooltip.basedefense.unlockids").applyTextStyle(
                    TextFormatting.GRAY
                )
            );
            for (long id : ids) {
                tooltip.add(
                    new StringTextComponent("  " + String.format("%016X", id)).applyTextStyle(
                        TextFormatting.DARK_GRAY
                    )
                );
            }
        }
    }

    @Override
    public abstract boolean onUnlock(LockContext ctx);
}
