package sciwhiz12.basedefense.util;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import sciwhiz12.basedefense.capabilities.CodedLock;
import sciwhiz12.basedefense.init.ModCapabilities;

public final class Util {
    public static IWorldPosCallable getOrDummy(@Nullable World world, @Nullable BlockPos pos) {
        if (world != null && pos != null) { return IWorldPosCallable.of(world, pos); }
        return IWorldPosCallable.DUMMY;
    }

    public static <F, S, U> U mapIfBothPresent(LazyOptional<F> first, LazyOptional<S> second, U defaultValue,
            BiFunction<F, S, U> func) {
        return first.map((fi) -> second.map((se) -> func.apply(fi, se)).orElse(defaultValue)).orElse(defaultValue);
    }

    public static <F, S> void consumeIfPresent(LazyOptional<F> first, LazyOptional<S> second, BiConsumer<F, S> consumer) {
        first.ifPresent((fi) -> { second.ifPresent((se) -> consumer.accept(fi, se)); });
    }

    public static void addLockInformation(ItemStack stack, List<ITextComponent> tooltip) {
        List<Long> ids = stack.getCapability(ModCapabilities.LOCK).filter((lock) -> lock instanceof CodedLock).map((
                lock) -> ((CodedLock) lock).getCodes()).orElse(Collections.emptyList());
        if (ids.size() != 0) {
            tooltip.add(new TranslationTextComponent("tooltip.basedefense.unlockids").applyTextStyle(TextFormatting.GRAY));
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
                tooltip.add((new TranslationTextComponent("tooltip.basedefense.color", i + 1, String.format("#%06X",
                    colors[i]))).applyTextStyle(TextFormatting.GRAY));
            }
        }
    }
}
