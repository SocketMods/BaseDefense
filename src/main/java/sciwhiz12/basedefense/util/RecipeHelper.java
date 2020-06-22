package sciwhiz12.basedefense.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Helper for recipe serializers.
 *
 * @author SciWhiz12
 */
public class RecipeHelper {
    // Prevent instantiation
    private RecipeHelper() {}

    public static final Method DESERIALIZE_KEY = ObfuscationReflectionHelper.findMethod(
        ShapedRecipe.class, "func_192408_a", JsonObject.class
    );
    public static final Method SHRINK = ObfuscationReflectionHelper.findMethod(
        ShapedRecipe.class, "func_194134_a", String[].class
    );
    public static final Method PATTERN_FROM_JSON = ObfuscationReflectionHelper.findMethod(
        ShapedRecipe.class, "func_192407_a", JsonArray.class
    );
    public static final Method DESERIALIZE_INGREDIENTS = ObfuscationReflectionHelper.findMethod(
        ShapedRecipe.class, "func_192402_a", String[].class, Map.class, int.class, int.class
    );

    /**
     * A generic {@link IRecipeSerializer} for classes extending
     * {@link ShapedRecipe}. <br/>
     * Useful for recipes that behave like and extend {@code ShapedRecipe} to only
     * add additional custom logic.
     * 
     * @param <S> The {@code ShapedRecipe} that this serializer supports
     */
    public static class ShapedSerializer<S extends ShapedRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements
            IRecipeSerializer<S> {
        private final RecipeFactory<S> factory;

        public ShapedSerializer(RecipeFactory<S> factoryIn) {
            this.factory = checkNotNull(factoryIn);
        }

        @SuppressWarnings("unchecked")
        @Override
        public S read(ResourceLocation recipeId, JsonObject json) {
            try {
                String group = JSONUtils.getString(json, "group", "");
                Map<String, Ingredient> keyMap = (Map<String, Ingredient>) DESERIALIZE_KEY.invoke(
                    null, JSONUtils.getJsonObject(json, "key")
                );
                String[] patterns = (String[]) SHRINK.invoke(
                    null, PATTERN_FROM_JSON.invoke(null, JSONUtils.getJsonArray(json, "pattern"))
                );
                int width = patterns[0].length();
                int height = patterns.length;
                NonNullList<Ingredient> ingredients = (NonNullList<Ingredient>) DESERIALIZE_INGREDIENTS.invoke(
                    null, patterns, keyMap, width, height
                );
                ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
                return factory.create(recipeId, group, width, height, ingredients, output);
            }
            catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException("Error during deserialization!", e);
            }
        }

        @Override
        public S read(ResourceLocation recipeId, PacketBuffer buffer) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            String group = buffer.readString(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); ++i) { ingredients.set(i, Ingredient.read(buffer)); }

            ItemStack output = buffer.readItemStack();
            return factory.create(recipeId, group, width, height, ingredients, output);
        }

        @Override
        public void write(PacketBuffer buffer, S recipe) {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeString(recipe.getGroup());

            for (Ingredient ingredient : recipe.getIngredients()) { ingredient.write(buffer); }

            buffer.writeItemStack(recipe.getRecipeOutput());
        }
    }

    /**
     * Factory for creating {@link ShapedRecipe}. <br/>
     * Used to reference constructors in {@link RecipeHelper}.
     * 
     * @param <R> The {@code ShapedRecipe} that this creates
     */
    @FunctionalInterface
    public interface RecipeFactory<R extends ShapedRecipe> {
        R create(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn,
                NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn);
    }
}
