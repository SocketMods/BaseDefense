package sciwhiz12.basedefense.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;

/**
 * Helper methods for {@link ItemStack}s and codes.
 *
 * @author SciWhiz12
 */
public final class ItemHelper {
    // Prevent instantiation
    private ItemHelper() {}

    public static void addCodeInformation(ItemStack stack, @Nullable List<ITextComponent> tooltip) {
        checkNotNull(stack);
        tooltip = tooltip != null ? tooltip : new ArrayList<>();
        List<Long> ids = stack.getCapability(CODE_HOLDER).map(ICodeHolder::getCodes).orElse(Collections.emptyList());
        if (ids.size() != 0) {
            tooltip.add(new TranslationTextComponent("tooltip.basedefense.storedcodes").applyTextStyle(TextFormatting.GRAY));
            for (long id : ids) {
                tooltip.add(
                    new StringTextComponent("  " + String.format("%016X", id)).applyTextStyle(TextFormatting.DARK_GRAY)
                );
            }
        }
    }

    public static void addColorInformation(ItemStack stack, @Nullable List<ITextComponent> tooltip) {
        checkNotNull(stack);
        tooltip = tooltip != null ? tooltip : new ArrayList<>();
        CompoundNBT tag = stack.getChildTag("display");
        if (stack.hasTag() && tag != null && tag.contains("colors")) {
            int[] colors = tag.getIntArray("colors");
            for (int i = 0; i < colors.length; i++) {
                tooltip.add(
                    new TranslationTextComponent(
                        "tooltip.basedefense.color", i + 1, new StringTextComponent(String.format("#%06X", colors[i]))
                            .applyTextStyle(TextFormatting.DARK_GRAY)
                    ).applyTextStyle(TextFormatting.GRAY)
                );
            }
        }
    }

    @Nullable
    @SafeVarargs
    public static <T> CompoundNBT getItemShareTag(ItemStack stack, Capability<T>... caps) {
        checkNotNull(stack);
        checkNotNull(caps);
        CompoundNBT shareTag = new CompoundNBT();
        if (stack.hasTag()) { shareTag.put("Tag", stack.getTag()); }
        for (Capability<T> cap : caps) {
            stack.getCapability(cap).ifPresent(inst -> {
                INBT nbt = cap.writeNBT(inst, null);
                if (nbt != null) { shareTag.put(cap.getName(), nbt); }
            });
        }
        return !shareTag.isEmpty() ? shareTag : null;
    }

    @SafeVarargs
    public static <T> void readItemShareTag(ItemStack stack, @Nullable CompoundNBT nbt, Capability<T>... caps) {
        checkNotNull(stack);
        checkNotNull(caps);
        if (nbt == null) { return; }
        if (nbt.contains("Tag", Constants.NBT.TAG_COMPOUND)) { stack.setTag(nbt.getCompound("Tag")); }
        for (Capability<T> cap : caps) {
            if (nbt.contains(cap.getName())) {
                stack.getCapability(cap).ifPresent(inst -> cap.readNBT(inst, null, nbt.get(cap.getName())));
            }
        }
    }

    public static void copyCodes(ItemStack fromStack, ItemStack toStack) {
        checkNotNull(fromStack);
        checkNotNull(toStack);
        Util.consumeIfPresent(
            fromStack.getCapability(CODE_HOLDER), toStack.getCapability(CODE_HOLDER), (from, to) -> to.setCodes(
                from.getCodes()
            )
        );
    }
}
