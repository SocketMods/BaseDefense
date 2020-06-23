package sciwhiz12.basedefense.datagen;

import static net.minecraft.state.properties.DoubleBlockHalf.LOWER;

import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraft.world.storage.loot.conditions.SurvivesExplosion;
import net.minecraft.world.storage.loot.functions.CopyNbt;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.init.ModItems;

public class LootTables extends BaseLootTableProvider {
    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        addStandardDropTable(ModBlocks.TEST_LOCK_BLOCK);
        addStandardDropTable(ModBlocks.LOCKSMITH_TABLE);
        addStandardDropTable(ModBlocks.KEYSMITH_TABLE);

        lockedDoor(ModBlocks.LOCKED_OAK_DOOR);
        lockedDoor(ModBlocks.LOCKED_BIRCH_DOOR);
        lockedDoor(ModBlocks.LOCKED_SPRUCE_DOOR);
        lockedDoor(ModBlocks.LOCKED_JUNGLE_DOOR);
        lockedDoor(ModBlocks.LOCKED_ACACIA_DOOR);
        lockedDoor(ModBlocks.LOCKED_DARK_OAK_DOOR);
        lockedDoor(ModBlocks.LOCKED_IRON_DOOR);

        padlockedDoor(ModBlocks.PADLOCKED_OAK_DOOR);
        padlockedDoor(ModBlocks.PADLOCKED_BIRCH_DOOR);
        padlockedDoor(ModBlocks.PADLOCKED_SPRUCE_DOOR);
        padlockedDoor(ModBlocks.PADLOCKED_JUNGLE_DOOR);
        padlockedDoor(ModBlocks.PADLOCKED_ACACIA_DOOR);
        padlockedDoor(ModBlocks.PADLOCKED_DARK_OAK_DOOR);
        padlockedDoor(ModBlocks.PADLOCKED_IRON_DOOR);
    }

    void lockedDoor(LockedDoorBlock block) {
        StatePropertiesPredicate.Builder predicate = StatePropertiesPredicate.Builder.newBuilder().withProp(
            LockedDoorBlock.HALF, LOWER
        );

        LootPool.Builder builder = createStandardDrops(block.baseBlock);
        builder.acceptCondition(BlockStateProperty.builder(block).fromProperties(predicate));

        addTable(block, LootTable.builder().addLootPool(builder));
    }

    void padlockedDoor(PadlockedDoorBlock block) {
        StatePropertiesPredicate.Builder predicate = StatePropertiesPredicate.Builder.newBuilder().withProp(
            PadlockedDoorBlock.HALF, LOWER
        );

        LootPool.Builder doorItem = createStandardDrops(block.baseBlock);
        doorItem.acceptCondition(BlockStateProperty.builder(block).fromProperties(predicate));

        LootPool.Builder padlock = createStandardDrops(ModItems.BROKEN_PADLOCK);
        padlock.acceptCondition(BlockStateProperty.builder(block).fromProperties(predicate));
        CopyNbt.Builder copyFunc = CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY);
        copyFunc.addOperation("LockItem.tag", "{}", CopyNbt.Action.MERGE);
        padlock.acceptFunction(copyFunc);

        addTable(block, LootTable.builder().addLootPool(doorItem).addLootPool(padlock));
    }

    void addStandardDropTable(Block b) {
        addTable(b, LootTable.builder().addLootPool(createStandardDrops(b)));
    }

    void addTable(Block b, LootTable.Builder table) {
        lootTables.put(b, table);
    }

    LootPool.Builder createStandardDrops(IItemProvider itemProvider) {
        return LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(SurvivesExplosion.builder()).addEntry(
            ItemLootEntry.builder(itemProvider)
        );
    }
}
