package tk.sciwhiz12.basedefense.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.Reference.RecipeSerializers;
import tk.sciwhiz12.basedefense.item.IColorable;

import java.util.ArrayList;
import java.util.List;

public class ColoringRecipe extends CustomRecipe {
    public ColoringRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        final int width = inv.getWidth();
        final int height = inv.getHeight();
        int colors = 0;
        ItemStack colorItem = ItemStack.EMPTY;
        if (inv.isEmpty()) {
            return false;
        }
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                ItemStack stack = inv.getItem(row * width + col);
                if (stack.isEmpty()) {
                    continue;
                }
                if (stack.getItem() instanceof IColorable) {
                    if (colorItem.isEmpty()) {
                        colorItem = stack;
                        continue;
                    } else {
                        return false;
                    }
                }
                @Nullable DyeColor color = DyeColor.getColor(stack);
                if (color != null) {
                    colors++;
                }
                if (colors > 3) {
                    return false;
                }
            }
        }
        return colors > 0 && !colorItem.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        final int width = inv.getWidth();
        final int height = inv.getHeight();
        List<DyeColor> colors = new ArrayList<>(4);
        ItemStack colorItem = ItemStack.EMPTY;
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                ItemStack stack = inv.getItem(row * width + col);
                if (stack.isEmpty()) {
                    continue;
                }
                if (stack.getItem() instanceof IColorable) {
                    if (colorItem.isEmpty()) {
                        colorItem = stack;
                        continue;
                    } else {
                        return ItemStack.EMPTY;
                    }
                }
                @Nullable DyeColor color = DyeColor.getColor(stack);
                if (color != null) {
                    colors.add(color);
                }
                if (colors.size() > 3) {
                    return ItemStack.EMPTY;
                }
            }
        }
        if (colors.size() == 0) {
            return ItemStack.EMPTY;
        }
        if (colorItem.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack output = colorItem.copy();
        if (output.getItem() instanceof IColorable color) {
            for (int idx = 0; idx < colors.size(); idx++) {
                color.setColor(output, idx, colors.get(idx).getTextColor());
            }
        }
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 4;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.COLORING;
    }
}
