package sciwhiz12.basedefense.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
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
}
