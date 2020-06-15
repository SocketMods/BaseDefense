package sciwhiz12.basedefense.item;

import java.util.Arrays;

import com.google.common.base.Preconditions;

import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public interface IColorable {
    default boolean hasColors(ItemStack stack) {
        Preconditions.checkNotNull(stack);
        if (!stack.isEmpty() && stack.getItem() instanceof IColorable) {
            CompoundNBT display = stack.getChildTag("display");
            if (display != null) { return display.getIntArray("colors").length > 0; }
        }
        return false;
    }

    default void setColor(ItemStack stack, int index, DyeColor color) {
        Preconditions.checkNotNull(stack);
        Preconditions.checkNotNull(color);
        if (index < 0) { throw new IllegalArgumentException(String.valueOf(index)); }
        if (!stack.isEmpty() && stack.getItem() instanceof IColorable) {
            CompoundNBT display = stack.getOrCreateChildTag("display");
            int[] colors = display.getIntArray("colors");
            if (colors.length <= index) { colors = Arrays.copyOf(colors, index + 1); }
            colors[index] = color.getColorValue();
            display.putIntArray("colors", colors);
        }
    }

    default void setColors(ItemStack stack, int[] colors) {
        Preconditions.checkNotNull(stack);
        Preconditions.checkNotNull(colors);
        if (!stack.isEmpty() && stack.getItem() instanceof IColorable) {
            CompoundNBT display = stack.getOrCreateChildTag("display");
            display.putIntArray("colors", colors);
        }
    }

    default int[] getColors(ItemStack stack) {
        Preconditions.checkNotNull(stack);
        if (!stack.isEmpty() && stack.getItem() instanceof IColorable && stack.hasTag()) {
            CompoundNBT display = stack.getChildTag("display");
            if (display != null) { return display.getIntArray("colors"); }
        }
        return new int[0];
    }

    default DyeColor getColor(ItemStack stack, int index) {
        Preconditions.checkNotNull(stack);
        if (index < 0) { throw new IllegalArgumentException(String.valueOf(index)); }
        if (!stack.isEmpty() && stack.getItem() instanceof IColorable && stack.hasTag()) {
            CompoundNBT display = stack.getChildTag("display");
            if (display != null) {
                int[] colors = display.getIntArray("colors");
                if (colors.length - index > 0) { return fromColorValue(colors[index]); }
            }
        }
        return DyeColor.WHITE;
    }

    static DyeColor fromColorValue(int value) {
        for (DyeColor color : DyeColor.values()) { if (color.getColorValue() == value) { return color; } }
        return DyeColor.WHITE;
    }

    static void copyColors(ItemStack from, ItemStack to) {
        Preconditions.checkNotNull(from);
        Preconditions.checkNotNull(to);
        if (from.isEmpty() || to.isEmpty()) { return; }
        if (from.getItem() instanceof IColorable && to.getItem() instanceof IColorable) {
            IColorable fromItem = (IColorable) from.getItem();
            IColorable toItem = (IColorable) to.getItem();
            toItem.setColors(to, fromItem.getColors(from));
        }
    }
}
