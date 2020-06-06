package sciwhiz12.basedefense.recipe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sciwhiz12.basedefense.init.ModRecipes;
import sciwhiz12.basedefense.item.lock.LockCoreItem;

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
                    output.setTagInfo("LockItem", stack.write(new CompoundNBT()));
                    if (stack.hasDisplayName()) { output.setDisplayName(stack.getDisplayName()); }
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

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements
            IRecipeSerializer<LockedDoorRecipe> {
        private static final Method DESERIALIZE_KEY = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class,
            "func_192408_a", JsonObject.class);
        private static final Method SHRINK = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class, "func_194134_a",
            String[].class);
        private static final Method PATTERN_FROM_JSON = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class,
            "func_192407_a", JsonArray.class);
        private static final Method DESERIALIZE_INGREDIENTS = ObfuscationReflectionHelper.findMethod(ShapedRecipe.class,
            "func_192402_a", String[].class, Map.class, int.class, int.class);

        @SuppressWarnings("unchecked")
        @Override
        public LockedDoorRecipe read(ResourceLocation recipeId, JsonObject json) {
            try {
                String s = JSONUtils.getString(json, "group", "");
                Map<String, Ingredient> map = (Map<String, Ingredient>) DESERIALIZE_KEY.invoke(null, JSONUtils.getJsonObject(
                    json, "key"));
                String[] astring = (String[]) SHRINK.invoke(null, PATTERN_FROM_JSON.invoke(null, JSONUtils.getJsonArray(json,
                    "pattern")));
                int i = astring[0].length();
                int j = astring.length;
                NonNullList<Ingredient> nonnulllist = (NonNullList<Ingredient>) DESERIALIZE_INGREDIENTS.invoke(null, astring,
                    map, i, j);
                ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
                return new LockedDoorRecipe(recipeId, s, i, j, nonnulllist, itemstack);
            }
            catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException("Error during deserialization!", e);
            }
        }

        @Override
        public LockedDoorRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int i = buffer.readVarInt();
            int j = buffer.readVarInt();
            String s = buffer.readString(32767);
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

            for (int k = 0; k < nonnulllist.size(); ++k) { nonnulllist.set(k, Ingredient.read(buffer)); }

            ItemStack itemstack = buffer.readItemStack();
            return new LockedDoorRecipe(recipeId, s, i, j, nonnulllist, itemstack);
        }

        @Override
        public void write(PacketBuffer buffer, LockedDoorRecipe recipe) {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeString(recipe.getGroup());

            for (Ingredient ingredient : recipe.getIngredients()) { ingredient.write(buffer); }

            buffer.writeItemStack(recipe.getRecipeOutput());
        }
    }
}
