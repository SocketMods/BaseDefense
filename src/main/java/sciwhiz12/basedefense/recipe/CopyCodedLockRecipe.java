package sciwhiz12.basedefense.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import sciwhiz12.basedefense.Reference.RecipeSerializers;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.util.ItemHelper;

import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;

public class CopyCodedLockRecipe extends ShapedRecipe {
    public CopyCodedLockRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn,
            NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
    }

    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack output = this.getRecipeOutput().copy();
        for (int row = 0; row < inv.getHeight(); row++) {
            for (int col = 0; col < inv.getWidth(); col++) {
                ItemStack stack = inv.getStackInSlot(row + col * inv.getWidth());
                if (!stack.isEmpty() && stack.getCapability(LOCK).isPresent()) {
                    IColorable.copyColors(stack, output);
                    ItemHelper.copyCodes(stack, output);
                    if (stack.hasDisplayName()) { output.setDisplayName(stack.getDisplayName()); }
                    break;
                }
            }
        }
        return output;
    }

    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.COPY_LOCK;
    }
}
