package tk.sciwhiz12.basedefense.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import tk.sciwhiz12.basedefense.Reference.RecipeSerializers;
import tk.sciwhiz12.basedefense.item.IColorable;
import tk.sciwhiz12.basedefense.util.ItemHelper;

import static com.google.common.base.Preconditions.checkArgument;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;

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

    public ItemStack assemble(CraftingContainer inv) {
        ItemStack output = this.getResultItem().copy();
        for (int row = 0; row < inv.getHeight(); row++) {
            for (int col = 0; col < inv.getWidth(); col++) {
                ItemStack stack = inv.getItem(row + col * inv.getWidth());
                if (!stack.isEmpty() && stack.getCapability(CODE_HOLDER).isPresent()) {
                    IColorable.copyColors(stack, output);
                    ItemHelper.copyCodes(stack, output);
                    if (stack.hasCustomHoverName()) {
                        output.setHoverName(stack.getHoverName());
                    }
                    break;
                }
            }
        }
        return output;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.CODED_LOCK;
    }
}
