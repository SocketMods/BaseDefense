package sciwhiz12.basedefense.item.lock;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import sciwhiz12.basedefense.LockingUtil;

public class BrokenPadlockItem extends Item {
    private static final IItemPropertyGetter COLOR_GETTER = (stack, world, livingEntity) -> {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) { return (float) tag.getIntArray("colors").length; }
        return 0.0F;
    };
    
    public BrokenPadlockItem() {
        super(new Item.Properties().maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("colors"), COLOR_GETTER);
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!flagIn.isAdvanced()) return;
        long[] ids = LockingUtil.getUnlockIDs(stack);
        if (ids.length != 0) {
            tooltip.add(new TranslationTextComponent("tooltip.basedefense.unlockids").applyTextStyle(TextFormatting.GRAY));
            for (long id : ids) {
                tooltip.add(
                    new StringTextComponent("  " + String.format("%016X", id)).applyTextStyle(TextFormatting.DARK_GRAY)
                );
            }
        }
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) {
            int[] colors = tag.getIntArray("colors");
            for (int i = 0; i < colors.length; i++) {
                tooltip.add(
                    (new TranslationTextComponent("tooltip.basedefense.keycolor", i + 1, String.format("#%06X", colors[i])))
                        .applyTextStyle(TextFormatting.GRAY)
                );
            }
        }
    }
}
