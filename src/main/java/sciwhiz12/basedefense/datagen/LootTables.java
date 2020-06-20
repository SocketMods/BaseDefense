package sciwhiz12.basedefense.datagen;

import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.DoubleBlockHalf;
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

        StatePropertiesPredicate.Builder lowerPredicate;

        LockedDoorBlock[] lockedDoorBlocks = { ModBlocks.LOCKED_OAK_DOOR, ModBlocks.LOCKED_BIRCH_DOOR,
                ModBlocks.LOCKED_SPRUCE_DOOR, ModBlocks.LOCKED_JUNGLE_DOOR, ModBlocks.LOCKED_ACACIA_DOOR,
                ModBlocks.LOCKED_DARK_OAK_DOOR, ModBlocks.LOCKED_IRON_DOOR };
        lowerPredicate = StatePropertiesPredicate.Builder.newBuilder().withProp(LockedDoorBlock.HALF, DoubleBlockHalf.LOWER);
        for (LockedDoorBlock lockedDoor : lockedDoorBlocks) {
            LootPool.Builder builder = LootPool.builder().rolls(ConstantRange.of(1));
            builder.acceptCondition(SurvivesExplosion.builder());
            builder.acceptCondition(BlockStateProperty.builder(lockedDoor).fromProperties(lowerPredicate));
            builder.addEntry(ItemLootEntry.builder(lockedDoor.baseBlock));
            addTable(lockedDoor, LootTable.builder().addLootPool(builder));
        }

        PadlockedDoorBlock[] padlockedDoorBlocks = { ModBlocks.PADLOCKED_OAK_DOOR, ModBlocks.PADLOCKED_BIRCH_DOOR,
                ModBlocks.PADLOCKED_SPRUCE_DOOR, ModBlocks.PADLOCKED_JUNGLE_DOOR, ModBlocks.PADLOCKED_ACACIA_DOOR,
                ModBlocks.PADLOCKED_DARK_OAK_DOOR, ModBlocks.PADLOCKED_IRON_DOOR };
        lowerPredicate = StatePropertiesPredicate.Builder.newBuilder().withProp(
            PadlockedDoorBlock.HALF, DoubleBlockHalf.LOWER
        );
        for (PadlockedDoorBlock padlockedDoor : padlockedDoorBlocks) {
            LootPool.Builder doorItem = createStandardDrops(padlockedDoor.baseBlock);
            doorItem.acceptCondition(BlockStateProperty.builder(padlockedDoor).fromProperties(lowerPredicate));

            LootPool.Builder padlock = createStandardDrops(ModItems.BROKEN_PADLOCK);
            padlock.acceptCondition(BlockStateProperty.builder(padlockedDoor).fromProperties(lowerPredicate));
            CopyNbt.Builder copyFunc = CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY);
            copyFunc.addOperation("LockItem.tag", "{}", CopyNbt.Action.MERGE);
            padlock.acceptFunction(copyFunc);

            addTable(padlockedDoor, LootTable.builder().addLootPool(doorItem).addLootPool(padlock));
        }
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
