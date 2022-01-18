package tk.sciwhiz12.basedefense.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
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
    private RecipeHelper() {
    }

    public static final Method DESERIALIZE_KEY = ObfuscationReflectionHelper
        .findMethod(ShapedRecipe.class, "m_44210" + "_", JsonObject.class);
    public static final Method SHRINK = ObfuscationReflectionHelper
        .findMethod(ShapedRecipe.class, "m_44186" + "_", String[].class);
    public static final Method PATTERN_FROM_JSON = ObfuscationReflectionHelper
        .findMethod(ShapedRecipe.class, "m_44196" + "_", JsonArray.class);
    public static final Method DESERIALIZE_INGREDIENTS = ObfuscationReflectionHelper
        .findMethod(ShapedRecipe.class, "m_44202" + "_", String[].class, Map.class, int.class, int.class);

    /**
     * <p>A generic {@link RecipeSerializer} for classes extending {@link ShapedRecipe}.</p>
     *
     * <p>Useful for recipes that extend {@code ShapedRecipe} that does not add additional data,
     * but only toadd additional custom logic.</p>
     *
     * @param <S> The {@code ShapedRecipe} that this serializer supports
     */
    public static class ShapedSerializer<S extends ShapedRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>>
        implements RecipeSerializer<S> {
        private final RecipeFactory<S> factory;

        public ShapedSerializer(RecipeFactory<S> factoryIn) {
            this.factory = checkNotNull(factoryIn);
        }

        @SuppressWarnings("unchecked")
        @Override
        public S fromJson(ResourceLocation recipeId, JsonObject json) {
            try {
                String group = GsonHelper.getAsString(json, "group", "");
                Map<String, Ingredient> keyMap = (Map<String, Ingredient>) DESERIALIZE_KEY
                    .invoke(null, GsonHelper.getAsJsonObject(json, "key"));
                String[] patterns = (String[]) SHRINK
                    .invoke(null, PATTERN_FROM_JSON.invoke(null, GsonHelper.getAsJsonArray(json, "pattern")));
                int width = patterns[0].length();
                int height = patterns.length;
                NonNullList<Ingredient> ingredients = (NonNullList<Ingredient>) DESERIALIZE_INGREDIENTS
                    .invoke(null, patterns, keyMap, width, height);
                ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
                return factory.create(recipeId, group, width, height, ingredients, output);
            } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException("Error during deserialization!", e);
            }
        }

        @Override
        public S fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            String group = buffer.readUtf(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); ++i) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack output = buffer.readItem();
            return factory.create(recipeId, group, width, height, ingredients, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, S recipe) {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeUtf(recipe.getGroup());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

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
