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
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import sciwhiz12.basedefense.Util;
import sciwhiz12.basedefense.capabilities.CodedKey;
import sciwhiz12.basedefense.capabilities.GenericCapabilityProvider;
import sciwhiz12.basedefense.init.ModCapabilities;
import sciwhiz12.basedefense.item.IColorable;

public class KeyItem extends Item implements IColorable {
    private static final IItemPropertyGetter COLOR_GETTER = (stack, world, livingEntity) -> {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) { return (float) tag.getIntArray("colors").length; }
        return 0.0F;
    };

    public KeyItem() {
        super(new Item.Properties().maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("colors"), COLOR_GETTER);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return world.isBlockLoaded(pos) && world.getTileEntity(pos) != null && world.getTileEntity(pos).getCapability(
            ModCapabilities.LOCK).isPresent();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!flagIn.isAdvanced()) return;
        long id = (long) Util.applyOrDefault(stack.getCapability(ModCapabilities.KEY), -1, (key) -> {
            if (key instanceof CodedKey) { return ((CodedKey) key).getCode(); }
            return -1;
        });
        if (id != -1) {
            tooltip.add(new TranslationTextComponent("tooltip.basedefense.keyid", String.format("%016X", id)).applyTextStyle(
                TextFormatting.GRAY));
        }
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) {
            int[] colors = tag.getIntArray("colors");
            for (int i = 0; i < colors.length; i++) {
                tooltip.add((new TranslationTextComponent("tooltip.basedefense.keycolor", i + 1, String.format("#%06X",
                    colors[i]))).applyTextStyle(TextFormatting.GRAY));
            }
        }

    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new GenericCapabilityProvider<>(ModCapabilities.KEY, CodedKey::new);
    }
}
