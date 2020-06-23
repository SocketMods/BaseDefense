package sciwhiz12.basedefense.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import sciwhiz12.basedefense.init.ModRecipes;
import sciwhiz12.basedefense.item.lock.LockCoreItem;
import sciwhiz12.basedefense.item.LockedDoorBlockItem;

public class LockedDoorRecipe extends ShapedRecipe {
    public LockedDoorRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn,
            NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack output = this.getRecipeOutput().copy();
        for (int row = 0; row < inv.getHeight(); row++) {
            for (int col = 0; col < inv.getWidth(); col++) {
                ItemStack stack = inv.getStackInSlot(row + col * inv.getWidth());
                if (!stack.isEmpty() && stack.getItem() instanceof LockCoreItem) {
                    if (output.getItem() instanceof LockedDoorBlockItem) {
                        ((LockedDoorBlockItem) output.getItem()).setLockStack(output, stack);
                    } else {
                        output.setTagInfo("LockItem", stack.write(new CompoundNBT()));
                        if (stack.hasDisplayName()) { output.setDisplayName(stack.getDisplayName()); }
                    }
                    break;
                }
            }
        }
        return output;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.LOCKED_DOOR;
    }
}
