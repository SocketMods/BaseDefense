package sciwhiz12.basedefense.item.key;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import sciwhiz12.basedefense.LockingUtil;
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.ILockable;

public class KeyItem extends Item implements IKey {
    private static final IItemPropertyGetter COLOR_GETTER = (stack, world, livingEntity) -> {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) {
            return (float) tag.getIntArray("colors").length;
        }
        return 0.0F;
    };

    public KeyItem() {
        super(new Item.Properties().maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("colors"), COLOR_GETTER);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos,
            PlayerEntity player) {
        return world.isBlockLoaded(pos) && world.getBlockState(pos)
            .getBlock() instanceof ILockable;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn,
            List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!flagIn.isAdvanced()) return;
        long id = LockingUtil.getKeyID(stack);
        tooltip.add(
            new TranslationTextComponent("tooltip.basedefense.keyid", Long.toHexString(id))
                .applyTextStyle(TextFormatting.GRAY)
        );
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) {
            int[] colors = tag.getIntArray("colors");
            for (int i = 0; i < colors.length; i++) {
                tooltip.add(
                    (new TranslationTextComponent(
                        "tooltip.basedefense.keycolor", i + 1, String.format("#%06X", colors[i])
                    )).applyTextStyle(TextFormatting.GRAY)
                );
            }
        }

    }

    @Override
    public boolean canUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos,
            ILockable block, @Nullable PlayerEntity player) {
        return LockingUtil.hasUnlockID(lockStack, keyStack);
    }

    @Override
    public void onUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos,
            ILockable block, @Nullable PlayerEntity player) {
        return;
    }
}
