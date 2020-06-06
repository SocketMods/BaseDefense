package sciwhiz12.basedefense.item;

import java.util.Arrays;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public interface IColorable {
    public static final IItemColor ITEM_COLOR = (stack, tintIndex) -> {
        if (stack.getItem() instanceof IColorable && tintIndex >= 2) {
            return ((IColorable) stack.getItem()).getColor(stack, tintIndex - 2).getColorValue();
        }
        return -1;
    };

    default boolean hasColors(ItemStack stack) {
        if (stack == null) { throw new IllegalArgumentException("stack cannot be null"); }
        CompoundNBT display = stack.getChildTag("display");
        return !stack.isEmpty() && stack.getItem() instanceof IColorable && display != null && display.getIntArray(
            "colors").length > 0;
    }

    default void setColor(ItemStack stack, int index, DyeColor color) {
        if (stack == null) { throw new IllegalArgumentException("stack cannot be null"); }
        if (index < 0) { throw new IllegalArgumentException("index cannot be negative: " + index); }
        if (color == null) { throw new IllegalArgumentException("color cannot be null"); }
        if (!stack.isEmpty() && stack.getItem() instanceof IColorable) {
            CompoundNBT display = stack.getOrCreateChildTag("display");
            int[] colors = display.getIntArray("colors");
            if (colors.length <= index) { colors = Arrays.copyOf(colors, index + 1); }
            colors[index] = color.getColorValue();
            display.putIntArray("colors", colors);
        }
    }

    default void setColors(ItemStack stack, int[] colors) {
        if (stack == null) { throw new IllegalArgumentException("stack cannot be null"); }
        if (colors == null) { throw new IllegalArgumentException("colors cannot be null"); }
        if (!stack.isEmpty() && stack.getItem() instanceof IColorable) {
            CompoundNBT display = stack.getOrCreateChildTag("display");
            if (display != null) { display.putIntArray("colors", colors); }
        }
    }

    default int[] getColors(ItemStack stack) {
        if (stack == null) { throw new IllegalArgumentException("stack cannot be null"); }
        if (!stack.isEmpty() && stack.getItem() instanceof IColorable && stack.hasTag()) {
            CompoundNBT display = stack.getChildTag("display");
            if (display != null) { return display.getIntArray("colors"); }
        }
        return new int[0];
    }

    default DyeColor getColor(ItemStack stack, int index) {
        if (stack == null) { throw new IllegalArgumentException("stack cannot be null"); }
        if (index < 0) { throw new IllegalArgumentException("index cannot be negative: " + index); }
        if (!stack.isEmpty() && stack.getItem() instanceof IColorable && stack.hasTag()) {
            CompoundNBT display = stack.getChildTag("display");
            if (display != null) {
                int[] colors = display.getIntArray("colors");
                if (colors.length - index > 0) { return fromColorValue(colors[index]); }
            }
        }
        return DyeColor.WHITE;
    }

    public static DyeColor fromColorValue(int value) {
        for (DyeColor color : DyeColor.values()) { if (color.getColorValue() == value) { return color; } }
        return DyeColor.WHITE;
    }

    public static void copyColors(ItemStack from, ItemStack to) {
        if (from == null) { throw new IllegalArgumentException("from cannot be null"); }
        if (to == null) { throw new IllegalArgumentException("to cannot be null"); }
        if (from.isEmpty() || to.isEmpty()) { return; }
        if (from.getItem() instanceof IColorable && to.getItem() instanceof IColorable) {
            IColorable fromItem = (IColorable) from.getItem();
            IColorable toItem = (IColorable) to.getItem();
            toItem.setColors(to, fromItem.getColors(from));
        }
    }
}
