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

import static com.google.common.base.Preconditions.checkArgument;
import static sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;

public class CodedLockRecipe extends ShapedRecipe {
    public CodedLockRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn,
            NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
        final long lockCount = recipeItemsIn.stream().filter(LockedItemIngredient.class::isInstance)
                .map(LockedItemIngredient.class::cast).filter(LockedItemIngredient::requiresCode).count();
        checkArgument(lockCount == 1, "Expected 1 coded locked item ingredient, got %s: %s", lockCount, idIn);
        checkArgument(recipeOutputIn.getCapability(CODE_HOLDER).isPresent(),
                "Recipe output (%s) has no ICodeHolder capability: %s", recipeOutputIn.getItem().getRegistryName(), idIn);
        checkArgument(recipeOutputIn.getCapability(LOCK).isPresent(), "Recipe output (%s) has no ILock capability: %s",
                recipeOutputIn.getItem().getRegistryName(), idIn);
    }

    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack output = this.getRecipeOutput().copy();
        for (int row = 0; row < inv.getHeight(); row++) {
            for (int col = 0; col < inv.getWidth(); col++) {
                ItemStack stack = inv.getStackInSlot(row + col * inv.getWidth());
                if (!stack.isEmpty() && stack.getCapability(CODE_HOLDER).isPresent()) {
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
        return RecipeSerializers.CODED_LOCK;
    }
}
