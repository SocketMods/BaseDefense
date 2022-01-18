package tk.sciwhiz12.basedefense.datagen;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.*;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import tk.sciwhiz12.basedefense.Reference;
import tk.sciwhiz12.basedefense.Reference.Blocks;
import tk.sciwhiz12.basedefense.Reference.RecipeSerializers;
import tk.sciwhiz12.basedefense.block.LockedDoorBlock;
import tk.sciwhiz12.basedefense.recipe.LockedItemIngredient;

import java.util.function.Consumer;

import static tk.sciwhiz12.basedefense.Reference.MODID;
import static tk.sciwhiz12.basedefense.Reference.modLoc;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        // @formatter:off
        SpecialRecipeBuilder.special(RecipeSerializers.COLORING).save(consumer, modStr("coloring"));
        ShapedRecipeBuilder.shaped(Reference.Items.BLANK_KEY, 2)
                .pattern(" g ")
                .pattern(" in")
                .pattern(" in")
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('n', Tags.Items.NUGGETS_IRON)
                .unlockedBy("has_ingots", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLD_INGOT, Items.IRON_INGOT))
                .save(consumer, modLoc("blank_key"));
        CustomShapedRecipeBuilder.shaped(RecipeSerializers.CODED_LOCK, Reference.Items.PADLOCK)
                .pattern(" i ")
                .pattern("ICI")
                .pattern("GGG")
                .define('i', Tags.Items.NUGGETS_IRON)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('C', new LockedItemIngredient(Reference.Items.LOCK_CORE, true))
                .unlockedBy("has_lock_core", InventoryChangeTrigger.TriggerInstance.hasItems(Reference.Items.LOCK_CORE))
                .save(consumer, modLoc("padlock"));
        CustomShapedRecipeBuilder.shaped(RecipeSerializers.LOCKED_ITEM, Reference.Items.PORTABLE_SAFE)
                .pattern("iLi")
                .pattern("ici")
                .pattern("iPi")
                .define('i', Tags.Items.INGOTS_IRON)
                .define('c', Tags.Items.CHESTS_WOODEN)
                .define('P', ItemTags.PLANKS)
                .define('L', new LockedItemIngredient(Reference.Items.LOCK_CORE, false))
                .unlockedBy("has_lock_core", InventoryChangeTrigger.TriggerInstance.hasItems(Reference.Items.LOCK_CORE))
                .save(consumer, modLoc("portable_safe"));
        // @formatter:on
        lockedDoorRecipe(consumer, Blocks.LOCKED_OAK_DOOR);
        lockedDoorRecipe(consumer, Blocks.LOCKED_BIRCH_DOOR);
        lockedDoorRecipe(consumer, Blocks.LOCKED_SPRUCE_DOOR);
        lockedDoorRecipe(consumer, Blocks.LOCKED_JUNGLE_DOOR);
        lockedDoorRecipe(consumer, Blocks.LOCKED_ACACIA_DOOR);
        lockedDoorRecipe(consumer, Blocks.LOCKED_DARK_OAK_DOOR);
        lockedDoorRecipe(consumer, Blocks.LOCKED_IRON_DOOR);
        lockedDoorRecipe(consumer, Blocks.LOCKED_CRIMSON_DOOR);
        lockedDoorRecipe(consumer, Blocks.LOCKED_WARPED_DOOR);
    }

    void lockedDoorRecipe(Consumer<FinishedRecipe> consumer, LockedDoorBlock block) {
        // @formatter:off
        CustomShapedRecipeBuilder.shaped(RecipeSerializers.LOCKED_ITEM, block)
                .group("locked_door")
                .pattern("IdC")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('d', block.baseBlock)
                .define('C', new LockedItemIngredient(Reference.Items.LOCK_CORE, true))
                .unlockedBy("has_lock_core", InventoryChangeTrigger.TriggerInstance.hasItems(Reference.Items.LOCK_CORE))
                .save(consumer, block.getRegistryName());
        // @formatter:on
    }

    public static String modStr(String path) {
        return MODID + ":" + path;
    }
}
