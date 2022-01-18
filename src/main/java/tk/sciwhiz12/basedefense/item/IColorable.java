package tk.sciwhiz12.basedefense.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Interface for colorable items.
 *
 * @author SciWhiz12
 */
public interface IColorable {
    /**
     * @param stack The {@code ItemStack} to query
     * @return {@code true} if the stack has colors, otherwise {@code false}
     */
    default boolean hasColors(ItemStack stack) {
        checkNotNull(stack);
        if (!stack.isEmpty() && stack.getItem() == this) {
            @Nullable CompoundTag display = stack.getTagElement("display");
            if (display != null) {
                return display.getIntArray("colors").length > 0;
            }
        }
        return false;
    }

    /**
     * Sets the color of the stack at the index to the given color.
     *
     * @param stack The {@code ItemStack} to modify
     * @param index The color index to change
     * @param color The color to change to
     */
    default void setColor(ItemStack stack, int index, int color) {
        checkNotNull(stack);
        checkArgument(index >= 0, "Index is negative: %s", index);
        if (!stack.isEmpty() && stack.getItem() == this) {
            CompoundTag display = stack.getOrCreateTagElement("display");
            int[] colors = display.getIntArray("colors");
            if (colors.length <= index) {
                colors = Arrays.copyOf(colors, index + 1);
            }
            colors[index] = color;
            display.putIntArray("colors", colors);
        }
    }

    /**
     * Sets the colors of the given stack to the given array.
     *
     * @param stack  The {@code ItemStack} to modify
     * @param colors The array of new colors
     */
    default void setColors(ItemStack stack, int[] colors) {
        checkNotNull(stack);
        checkNotNull(colors);
        if (!stack.isEmpty() && stack.getItem() == this) {
            CompoundTag display = stack.getOrCreateTagElement("display");
            display.putIntArray("colors", colors);
        }
    }

    /**
     * Returns the current colors of the given stack.
     *
     * @param stack The {@code ItemStack} to query
     * @return the array of colors of the given stack, otherwise an empty array
     */
    default int[] getColors(ItemStack stack) {
        checkNotNull(stack);
        if (!stack.isEmpty() && stack.getItem() == this && stack.hasTag()) {
            @Nullable CompoundTag display = stack.getTagElement("display");
            if (display != null) {
                return display.getIntArray("colors");
            }
        }
        return new int[0];
    }

    /**
     * Gets the color at the specified index of the given stack.
     *
     * @param stack The {@code ItemStack} to query
     * @param index The color index to query
     * @return The color at that specified index, otherwise {@code 0}
     */
    default int getColor(ItemStack stack, int index) {
        checkNotNull(stack);
        checkArgument(index >= 0, "Index is negative: %s", index);
        if (!stack.isEmpty() && stack.getItem() == this && stack.hasTag()) {
            @Nullable CompoundTag display = stack.getTagElement("display");
            if (display != null) {
                int[] colors = display.getIntArray("colors");
                if (colors.length - index > 0) {
                    return colors[index];
                }
            }
        }
        return 0;
    }

    /**
     * Copies the colors from the first {@link ItemStack} to the second
     * {@code ItemStack}.
     *
     * @param from The source {@code ItemStack}
     * @param to   The destination {@code ItemStack}
     */
    static void copyColors(ItemStack from, ItemStack to) {
        checkNotNull(from);
        checkNotNull(to);
        if (from.isEmpty() || to.isEmpty()) {
            return;
        }
        if (from.getItem() instanceof IColorable && to.getItem() instanceof IColorable) {
            IColorable fromItem = (IColorable) from.getItem();
            IColorable toItem = (IColorable) to.getItem();
            toItem.setColors(to, fromItem.getColors(from));
        }
    }
}
