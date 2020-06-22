package sciwhiz12.basedefense.datagen;

import static sciwhiz12.basedefense.BaseDefense.modLoc;

import java.util.function.Consumer;

import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.common.Tags;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.init.ModItems;
import sciwhiz12.basedefense.init.ModRecipes;

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        // @formatter:off
        CustomRecipeBuilder.customRecipe(asSpecial(ModRecipes.PADLOCK_REPAIR)).build(consumer, modStr("padlock_repair"));
        CustomRecipeBuilder.customRecipe(asSpecial(ModRecipes.COLORING)).build(consumer, modStr("coloring"));
        ShapedRecipeBuilder.shapedRecipe(ModItems.BLANK_KEY, 2)
                .patternLine(" g ")
                .patternLine(" in")
                .patternLine(" in")
                .key('g', Tags.Items.INGOTS_GOLD)
                .key('i', Tags.Items.INGOTS_IRON)
                .key('n', Tags.Items.NUGGETS_IRON)
                .addCriterion("has_ingots", InventoryChangeTrigger.Instance.forItems(Items.GOLD_INGOT, Items.IRON_INGOT))
                .build(consumer, modLoc("blank_key"));
        CustomShapedRecipeBuilder.shapedRecipe(ModRecipes.COPY_LOCK, ModItems.PADLOCK)
                .patternLine(" i ")
                .patternLine("ICI")
                .patternLine("GGG")
                .key('i', Tags.Items.NUGGETS_IRON)
                .key('I', Tags.Items.INGOTS_IRON)
                .key('G', Tags.Items.INGOTS_GOLD)
                .key('C', ModItems.LOCK_CORE)
                .addCriterion("has_lock_core", InventoryChangeTrigger.Instance.forItems(ModItems.LOCK_CORE))
                .build(consumer, modLoc("padlock"));

        LockedDoorBlock[] lockedDoorBlocks = { ModBlocks.LOCKED_OAK_DOOR, ModBlocks.LOCKED_BIRCH_DOOR,
                ModBlocks.LOCKED_SPRUCE_DOOR, ModBlocks.LOCKED_JUNGLE_DOOR, ModBlocks.LOCKED_ACACIA_DOOR,
                ModBlocks.LOCKED_DARK_OAK_DOOR, ModBlocks.LOCKED_IRON_DOOR };
        for (LockedDoorBlock lockedDoor : lockedDoorBlocks) {
            CustomShapedRecipeBuilder.shapedRecipe(ModRecipes.LOCKED_DOOR, lockedDoor)
                    .setGroup("locked_door")
                    .patternLine("IdC")
                    .key('I', Tags.Items.INGOTS_IRON)
                    .key('d', lockedDoor.baseBlock)
                    .key('C', ModItems.LOCK_CORE)
                    .addCriterion("has_lock_core", InventoryChangeTrigger.Instance.forItems(ModItems.LOCK_CORE))
                    .build(consumer, lockedDoor.getRegistryName());
        }
        // @formatter:on
    }

    <T extends IRecipe<?>> SpecialRecipeSerializer<T> asSpecial(IRecipeSerializer<T> serializer) {
        return (SpecialRecipeSerializer<T>) serializer;
    }

    public static String modStr(String path) {
        return BaseDefense.MODID + ":" + path;
    }
}
