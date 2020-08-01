package sciwhiz12.basedefense.util;

import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * General utility methods.
 *
 * @author SciWhiz12
 */
public final class Util {
    // Prevent instantiation
    private Util() {}

    /**
     * >A fix for IntelliJ IDEA's "Constant conditions" inspection. Used for
     * {@link net.minecraftforge.registries.ObjectHolder} and
     * {@link net.minecraftforge.common.capabilities.CapabilityInject} fields.
     *
     * @return {@code null}
     */
    @Nonnull
    public static <T> T Null() {
        return null;
    }

    /**
     * Returns an {@link IWorldPosCallable} containing the given parameters, or
     * {@link IWorldPosCallable#DUMMY} if either parameter is {@code null}.
     *
     * @param world A {@link World}, or {@code null}
     * @param pos   A {@link BlockPos} or {@code null}
     * @return an {@link IWorldPosCallable}, or {@link IWorldPosCallable#DUMMY}
     */
    public static IWorldPosCallable getOrDummy(@Nullable World world, @Nullable BlockPos pos) {
        if (world != null && pos != null) { return IWorldPosCallable.of(world, pos); }
        return IWorldPosCallable.DUMMY;
    }

    /**
     * If both {@link LazyOptional}s are present, applies their values to
     * {@link BiFunction} and returns the resulting value, otherwise returns the
     * given default value.
     *
     * @param <F>          The enclosing type of the first {@code LazyOptional}
     * @param <S>          The enclosing type of the second {@code LazyOptional}
     * @param <U>          The type of the return value
     * @param first        The first {@code LazyOptional}
     * @param second       The second {@code LazyOptional}
     * @param defaultValue The default return value
     * @param func         The {@code BiFunction} to be applied
     * @return The result of the {@link BiFunction} if both {@link LazyOptional}s
     * are present, otherwise the default return value
     */
    public static <F, S, U> U mapIfBothPresent(LazyOptional<F> first, LazyOptional<S> second, U defaultValue,
            BiFunction<F, S, U> func) {
        checkNotNull(first);
        checkNotNull(second);
        checkNotNull(func);
        checkNotNull(defaultValue);
        return first.map((fi) -> second.map((se) -> func.apply(fi, se)).orElse(defaultValue)).orElse(defaultValue);
    }

    /**
     * If both {@link LazyOptional}s are present, applies the values to the given
     * {@link BiConsumer}.
     *
     * @param <F>      The enclosing type of the first {@code LazyOptional}
     * @param <S>      The enclosing type of the second {@code LazyOptional}
     * @param first    The first {@code LazyOptional}
     * @param second   The second {@code LazyOptional}
     * @param consumer The {@code BiConsumer} of the values
     */
    public static <F, S> void consumeIfPresent(LazyOptional<F> first, LazyOptional<S> second, BiConsumer<F, S> consumer) {
        checkNotNull(first);
        checkNotNull(second);
        checkNotNull(consumer);
        first.ifPresent((fi) -> second.ifPresent((se) -> consumer.accept(fi, se)));
    }

    /**
     * Appends the given string to the given {@link ResourceLocation}'s path.
     *
     * @param base   The base {@code ResourceLocation}
     * @param append The string to append to the location's path
     * @return The location with path appendage
     */
    public static ResourceLocation appendPath(ResourceLocation base, String append) {
        checkNotNull(base);
        return new ResourceLocation(base.getNamespace(), base.getPath() + checkNotNull(append));
    }

    /**
     * <p>Creates a {@link TranslationTextComponent} based on if the given amount is {@code 0}, {@code 1}, or more than
     * {@code 1}.</p>
     *
     * <p>This method uses the following logic for determining the translation key:
     * <ul>
     *     <li>If the amount is {@code 0}, then append "{@code .zero}" to the base translation key.</li>
     *     <li>If the amount is {@code 1}, then append "{@code .one}" to the base translation key.</li>
     *     <li>If the amount is greater than {@code 1}, then the base translation key is used.</li>
     * </ul></p>
     *
     * <p>The parameters passed to the {@code TranslationTextComponent} construction are the selected translation key, the
     * amount, and any additional objects supplied to the method.</p>
     *
     * @param translationKeyBase The base translation key
     * @param amount             The amount to use for determining the translation key
     * @param additionalInfo     The additional objects when construction the {@code TranslationTextComponent}
     * @return The resulting {@code TranslationTextComponent}
     */
    public static TranslationTextComponent createAmountTooltip(String translationKeyBase, int amount,
            Object... additionalInfo) {
        if (amount == 0) {
            return new TranslationTextComponent(translationKeyBase + ".zero", amount, additionalInfo);
        } else if (amount == 1) {
            return new TranslationTextComponent(translationKeyBase + ".one", amount, additionalInfo);
        } else {
            return new TranslationTextComponent(translationKeyBase, amount, additionalInfo);
        }
    }
}
