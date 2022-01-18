package tk.sciwhiz12.basedefense.datagen;

import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.*;
import net.minecraft.item.Items;
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

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        // @formatter:off
        CustomRecipeBuilder.customRecipe(RecipeSerializers.COLORING).build(consumer, modStr("coloring"));
        ShapedRecipeBuilder.shapedRecipe(Reference.Items.BLANK_KEY, 2)
                .patternLine(" g ")
                .patternLine(" in")
                .patternLine(" in")
                .key('g', Tags.Items.INGOTS_GOLD)
                .key('i', Tags.Items.INGOTS_IRON)
                .key('n', Tags.Items.NUGGETS_IRON)
                .addCriterion("has_ingots", InventoryChangeTrigger.Instance.forItems(Items.GOLD_INGOT, Items.IRON_INGOT))
                .build(consumer, modLoc("blank_key"));
        CustomShapedRecipeBuilder.shapedRecipe(RecipeSerializers.CODED_LOCK, Reference.Items.PADLOCK)
                .patternLine(" i ")
                .patternLine("ICI")
                .patternLine("GGG")
                .key('i', Tags.Items.NUGGETS_IRON)
                .key('I', Tags.Items.INGOTS_IRON)
                .key('G', Tags.Items.INGOTS_GOLD)
                .key('C', new LockedItemIngredient(Reference.Items.LOCK_CORE, true))
                .addCriterion("has_lock_core", InventoryChangeTrigger.Instance.forItems(Reference.Items.LOCK_CORE))
                .build(consumer, modLoc("padlock"));
        CustomShapedRecipeBuilder.shapedRecipe(RecipeSerializers.LOCKED_ITEM, Reference.Items.PORTABLE_SAFE)
                .patternLine("iLi")
                .patternLine("ici")
                .patternLine("iPi")
                .key('i', Tags.Items.INGOTS_IRON)
                .key('c', Tags.Items.CHESTS_WOODEN)
                .key('P', ItemTags.PLANKS)
                .key('L', new LockedItemIngredient(Reference.Items.LOCK_CORE, false))
                .addCriterion("has_lock_core", InventoryChangeTrigger.Instance.forItems(Reference.Items.LOCK_CORE))
                .build(consumer, modLoc("portable_safe"));
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

    void lockedDoorRecipe(Consumer<IFinishedRecipe> consumer, LockedDoorBlock block) {
        // @formatter:off
        CustomShapedRecipeBuilder.shapedRecipe(RecipeSerializers.LOCKED_ITEM, block)
                .setGroup("locked_door")
                .patternLine("IdC")
                .key('I', Tags.Items.INGOTS_IRON)
                .key('d', block.baseBlock)
                .key('C', new LockedItemIngredient(Reference.Items.LOCK_CORE, true))
                .addCriterion("has_lock_core", InventoryChangeTrigger.Instance.forItems(Reference.Items.LOCK_CORE))
                .build(consumer, block.getRegistryName());
        // @formatter:on
    }

    public static String modStr(String path) {
        return MODID + ":" + path;
    }
}
