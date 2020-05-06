package sciwhiz12.basedefense.item.lock;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.LockContext;
import sciwhiz12.basedefense.init.BDItems;

public class SkeletonKeyItem extends Item implements IKey {
    public SkeletonKeyItem() {
        super(new Item.Properties().maxDamage(0).group(BDItems.GROUP));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn,
            List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(
                new TranslationTextComponent("tooltip.basedefense.skeleton_key")
                        .applyTextStyle(TextFormatting.RED)
        );
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canUnlock(LockContext context) {
        return true;
    }

    @Override
    public void unlock(LockContext context) {}
}
