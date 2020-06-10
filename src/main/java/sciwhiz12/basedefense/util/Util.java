package sciwhiz12.basedefense.util;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;
import sciwhiz12.basedefense.init.ModCapabilities;

public final class Util {
    @Nonnull
    public static <T> T Null() {
        return null;
    }

    public static IWorldPosCallable getOrDummy(@Nullable World world, @Nullable BlockPos pos) {
        if (world != null && pos != null) { return IWorldPosCallable.of(world, pos); }
        return IWorldPosCallable.DUMMY;
    }

    public static <F, S, U> U mapIfBothPresent(LazyOptional<F> first, LazyOptional<S> second, U defaultValue,
            BiFunction<F, S, U> func) {
        return first.map((fi) -> second.map((se) -> func.apply(fi, se)).orElse(defaultValue)).orElse(defaultValue);
    }

    public static <F, S> void consumeIfPresent(LazyOptional<F> first, LazyOptional<S> second, BiConsumer<F, S> consumer) {
        first.ifPresent((fi) -> second.ifPresent((se) -> consumer.accept(fi, se)));
    }

    public static void copyCodes(ItemStack fromStack, ItemStack toStack) {
        consumeIfPresent(fromStack.getCapability(ModCapabilities.CODE_HOLDER), toStack.getCapability(
            ModCapabilities.CODE_HOLDER), (from, to) -> to.setCodes(from.getCodes()));
    }

    public static void addCodeInformation(ItemStack stack, List<ITextComponent> tooltip) {
        List<Long> ids = stack.getCapability(ModCapabilities.CODE_HOLDER).filter((lock) -> lock instanceof ICodeHolder).map(
            ICodeHolder::getCodes).orElse(Collections.emptyList());
        if (ids != null && ids.size() != 0) {
            tooltip.add(new TranslationTextComponent("tooltip.basedefense.storedcodes").applyTextStyle(TextFormatting.GRAY));
            for (long id : ids) {
                tooltip.add(new StringTextComponent("  " + String.format("%016X", id)).applyTextStyle(
                    TextFormatting.DARK_GRAY));
            }
        }
    }

    public static void addColorInformation(ItemStack stack, List<ITextComponent> tooltip) {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) {
            int[] colors = tag.getIntArray("colors");
            for (int i = 0; i < colors.length; i++) {
                tooltip.add((new TranslationTextComponent("tooltip.basedefense.color", i + 1, new StringTextComponent(String
                    .format("#%06X", colors[i])).applyTextStyle(TextFormatting.DARK_GRAY))).applyTextStyle(
                        TextFormatting.GRAY));
            }
        }
    }

    @Nullable
    @SafeVarargs
    public static <T> CompoundNBT getItemShareTag(ItemStack stack, Capability<T>... caps) {
        CompoundNBT shareTag = new CompoundNBT();
        if (stack.hasTag()) { shareTag.put("Tag", stack.getTag()); }
        for (Capability<T> cap : caps) {
            INBT nbt = cap.writeNBT(stack.getCapability(cap).orElse(null), null);
            if (nbt != null) { shareTag.put(cap.getName(), nbt); }
        }
        return !shareTag.isEmpty() ? shareTag : null;
    }

    @SafeVarargs
    public static <T> void readItemShareTag(ItemStack stack, @Nullable CompoundNBT nbt, Capability<T>... caps) {
        if (nbt == null) { return; }
        if (nbt.contains("Tag", Constants.NBT.TAG_COMPOUND)) { stack.setTag(nbt.getCompound("Tag")); }
        for (Capability<T> cap : caps) {
            if (nbt.contains(cap.getName())) {
                T inst = stack.getCapability(cap).orElse(null);
                if (inst == null) { cap.readNBT(inst, null, nbt); }
            }
        }
    }
}
