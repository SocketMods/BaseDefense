package sciwhiz12.basedefense.util;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper for recipe serializers.
 *
 * @author SciWhiz12
 */
public class RecipeHelper {
    // Prevent instantiation
    private RecipeHelper() {}

    public static final Method DESERIALIZE_KEY = ObfuscationReflectionHelper
            .findMethod(ShapedRecipe.class, "keyFromJson", JsonObject.class);
    public static final Method SHRINK = ObfuscationReflectionHelper
            .findMethod(ShapedRecipe.class, "shrink", String[].class);
    public static final Method PATTERN_FROM_JSON = ObfuscationReflectionHelper
            .findMethod(ShapedRecipe.class, "patternFromJson", JsonArray.class);
    public static final Method DESERIALIZE_INGREDIENTS = ObfuscationReflectionHelper
            .findMethod(ShapedRecipe.class, "dissolvePattern", String[].class, Map.class, int.class, int.class);

    /**
     * <p>A generic {@link IRecipeSerializer} for classes extending {@link ShapedRecipe}.</p>
     *
     * <p>Useful for recipes that extend {@code ShapedRecipe} that does not add additional data,
     * but only toadd additional custom logic.</p>
     *
     * @param <S> The {@code ShapedRecipe} that this serializer supports
     */
    public static class ShapedSerializer<S extends ShapedRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<S> {
        private final RecipeFactory<S> factory;

        public ShapedSerializer(RecipeFactory<S> factoryIn) {
            this.factory = checkNotNull(factoryIn);
        }

        @SuppressWarnings("unchecked")
        @Override
        public S fromJson(ResourceLocation recipeId, JsonObject json) {
            try {
                String group = JSONUtils.getAsString(json, "group", "");
                Map<String, Ingredient> keyMap = (Map<String, Ingredient>) DESERIALIZE_KEY
                        .invoke(null, JSONUtils.getAsJsonObject(json, "key"));
                String[] patterns = (String[]) SHRINK
                        .invoke(null, PATTERN_FROM_JSON.invoke(null, JSONUtils.getAsJsonArray(json, "pattern")));
                int width = patterns[0].length();
                int height = patterns.length;
                NonNullList<Ingredient> ingredients = (NonNullList<Ingredient>) DESERIALIZE_INGREDIENTS
                        .invoke(null, patterns, keyMap, width, height);
                ItemStack output = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
                return factory.create(recipeId, group, width, height, ingredients, output);
            }
            catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException("Error during deserialization!", e);
            }
        }

        @Override
        public S fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            String group = buffer.readUtf(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); ++i) { ingredients.set(i, Ingredient.fromNetwork(buffer)); }

            ItemStack output = buffer.readItem();
            return factory.create(recipeId, group, width, height, ingredients, output);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, S recipe) {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeUtf(recipe.getGroup());

            for (Ingredient ingredient : recipe.getIngredients()) { ingredient.toNetwork(buffer); }

            buffer.writeItem(recipe.getResultItem());
        }
    }

    /**
     * <p>Factory for creating {@link ShapedRecipe}.</p>
     *
     * <p>Used to reference constructors in {@link RecipeHelper}.</p>
     *
     * @param <R> The {@code ShapedRecipe} that this creates
     */
    @FunctionalInterface
    public interface RecipeFactory<R extends ShapedRecipe> {
        R create(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn,
                NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn);
    }
}
