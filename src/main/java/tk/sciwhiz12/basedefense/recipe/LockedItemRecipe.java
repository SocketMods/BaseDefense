package tk.sciwhiz12.basedefense.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import tk.sciwhiz12.basedefense.Reference.RecipeSerializers;
import tk.sciwhiz12.basedefense.item.IContainsLockItem;

import static com.google.common.base.Preconditions.checkArgument;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;

public class LockedItemRecipe extends ShapedRecipe {
    public LockedItemRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> inputs,
                            ItemStack output) {
        super(id, group, width, height, inputs, output);
        final long lockCount = inputs.stream().filter(LockedItemIngredient.class::isInstance).count();
        checkArgument(lockCount == 1, "Expected 1 locked item ingredient, got %s: %s", lockCount, id);
        checkArgument(output.getItem() instanceof IContainsLockItem, "Recipe output (%s) cannot contain a lock item: %s",
            output.getItem().getRegistryName(), id);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack output = this.getResultItem().copy();
        for (int row = 0; row < inv.getHeight(); row++) {
            for (int col = 0; col < inv.getWidth(); col++) {
                ItemStack stack = inv.getItem(row + col * inv.getWidth());
                if (!stack.isEmpty() && stack.getCapability(LOCK).isPresent()) {
                    ((IContainsLockItem) output.getItem()).setLockStack(output, stack);
                    break;
                }
            }
        }
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.LOCKED_ITEM;
    }
}
