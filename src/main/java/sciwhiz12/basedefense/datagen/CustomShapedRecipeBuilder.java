package sciwhiz12.basedefense.datagen;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Copy of {@link net.minecraft.data.ShapedRecipeBuilder} that allows a custom
 * {@link IRecipeSerializer<ShapedRecipe>}.
 */
public class CustomShapedRecipeBuilder {
    private final IRecipeSerializer<? extends ShapedRecipe> serializer;
    private final Item result;
    private final int count;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    public CustomShapedRecipeBuilder(IRecipeSerializer<? extends ShapedRecipe> serializerIn, IItemProvider resultIn,
            int countIn) {
        this.serializer = serializerIn;
        this.result = resultIn.asItem();
        this.count = countIn;
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static CustomShapedRecipeBuilder shapedRecipe(IRecipeSerializer<? extends ShapedRecipe> serializerIn,
            IItemProvider resultIn) {
        return shapedRecipe(serializerIn, resultIn, 1);
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static CustomShapedRecipeBuilder shapedRecipe(IRecipeSerializer<? extends ShapedRecipe> serializerIn,
            IItemProvider resultIn, int countIn) {
        return new CustomShapedRecipeBuilder(serializerIn, resultIn, countIn);
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public CustomShapedRecipeBuilder key(Character symbol, ITag<Item> tagIn) {
        return this.key(symbol, Ingredient.fromTag(tagIn));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public CustomShapedRecipeBuilder key(Character symbol, IItemProvider itemIn) {
        return this.key(symbol, Ingredient.fromItems(itemIn));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public CustomShapedRecipeBuilder key(Character symbol, Ingredient ingredientIn) {
        checkArgument(!key.containsKey(symbol), "Symbol '%s' is already defined!", symbol);
        checkArgument(symbol != ' ', "Symbol ' ' (whitespace) is reserved and cannot be defined");
        this.key.put(symbol, ingredientIn);
        return this;
    }

    /**
     * Adds a new entry to the patterns for this recipe.
     */
    public CustomShapedRecipeBuilder patternLine(String patternIn) {
        checkArgument(
            pattern.isEmpty() || patternIn.length() == pattern.get(0).length(),
            "Pattern must be the same width on every line!"
        );
        this.pattern.add(patternIn);
        return this;
    }

    /**
     * Adds a criterion needed to unlock the recipe.
     */
    public CustomShapedRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
        this.advancementBuilder.withCriterion(name, criterionIn);
        return this;
    }

    public CustomShapedRecipeBuilder setGroup(String groupIn) {
        this.group = groupIn;
        return this;
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<IFinishedRecipe> consumerIn) {
        this.build(consumerIn, ForgeRegistries.ITEMS.getKey(this.result));
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}. Use
     * {@link #build(Consumer)} if save is the same as the ID for the result.
     */
    public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
        ResourceLocation resultLoc = ForgeRegistries.ITEMS.getKey(this.result);
        checkState(
            !new ResourceLocation(save).equals(resultLoc), "Shaped recipe %s should remove its 'save' argument", save
        );
        this.build(consumerIn, new ResourceLocation(save));
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion(
            "has_the_recipe", new RecipeUnlockedTrigger.Instance(EntityPredicate.AndPredicate.field_234582_a_, id)
        ).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        consumerIn.accept(
            new Result(
                id, serializer, result, count, group == null ? "" : group, pattern, key, advancementBuilder,
                new ResourceLocation(id.getNamespace(), "recipes/" + result.getGroup().getPath() + "/" + id.getPath())
            )
        );
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void validate(ResourceLocation id) {
        checkState(!pattern.isEmpty(), "No pattern is defined for shaped recipe %s!", id);
        Set<Character> set = Sets.newHashSet(this.key.keySet());
        set.remove(' ');

        for (String s : this.pattern) {
            for (int i = 0; i < s.length(); ++i) {
                char sym = s.charAt(i);
                checkState(key.containsKey(sym) || sym == ' ', "Pattern in recipe %s uses undefined symbol '%s'", id, sym);
                set.remove(sym);
            }
        }

        checkState(set.isEmpty(), "Ingredients are defined but not used in pattern for recipe %s", id);
        checkState(
            pattern.size() != 1 || pattern.get(0).length() != 1,
            "Shaped recipe %s only takes in a single item - should be a shapeless recipe", id
        );
        checkState(!advancementBuilder.getCriteria().isEmpty(), "No way of obtaining recipe %s", id);
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final IRecipeSerializer<? extends ShapedRecipe> serializer;
        private final Item result;
        private final int count;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation idIn, IRecipeSerializer<? extends ShapedRecipe> serializerIn, Item resultIn,
                int countIn, String groupIn, List<String> patternIn, Map<Character, Ingredient> keyIn,
                Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
            this.id = idIn;
            this.serializer = serializerIn;
            this.result = resultIn;
            this.count = countIn;
            this.group = groupIn;
            this.pattern = patternIn;
            this.key = keyIn;
            this.advancementBuilder = advancementBuilderIn;
            this.advancementId = advancementIdIn;
        }

        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) { json.addProperty("group", this.group); }

            JsonArray patternArr = new JsonArray();

            for (String s : this.pattern) { patternArr.add(s); }

            json.add("pattern", patternArr);
            JsonObject keysObj = new JsonObject();

            for (Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
                keysObj.add(String.valueOf(entry.getKey()), entry.getValue().serialize());
            }

            json.add("key", keysObj);
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
            if (this.count > 1) { resultObj.addProperty("count", this.count); }

            json.add("result", resultObj);
        }

        public IRecipeSerializer<?> getSerializer() {
            return serializer;
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getID() {
            return this.id;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is
         * no advancement.
         */
        @Nullable
        public JsonObject getAdvancementJson() {
            return this.advancementBuilder.serialize();
        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be
         * null if {@link #getAdvancementJson} is non-null.
         */
        @Nullable
        public ResourceLocation getAdvancementID() {
            return this.advancementId;
        }
    }
}
