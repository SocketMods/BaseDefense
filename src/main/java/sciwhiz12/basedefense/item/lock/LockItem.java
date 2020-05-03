package sciwhiz12.basedefense.item.lock;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
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

public class LockItem extends Item implements ILock {
    public LockItem() {
        super(new Item.Properties().maxDamage(0));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn,
            List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!Minecraft.getInstance().gameSettings.advancedItemTooltips) return;
        long[] ids = LockingUtil.getUnlockIDs(stack);
        if (ids.length != 0) {
            tooltip.add(
                    new TranslationTextComponent("tooltip.basedefense.unlockids").applyTextStyle(
                            TextFormatting.GRAY
                    )
            );
            for (long id : ids) {
                tooltip.add(
                        new StringTextComponent("  " + Long.toHexString(id)).applyTextStyle(
                                TextFormatting.DARK_GRAY
                        )
                );
            }
        }
    }

    @Override
    public void onUnlock(LockContext context) {}
}
