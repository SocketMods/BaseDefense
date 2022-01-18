package tk.sciwhiz12.basedefense.datagen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.SetContents;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import tk.sciwhiz12.basedefense.Reference.Blocks;
import tk.sciwhiz12.basedefense.Reference.Items;
import tk.sciwhiz12.basedefense.block.LockedDoorBlock;
import tk.sciwhiz12.basedefense.block.PadlockedDoorBlock;
import tk.sciwhiz12.basedefense.block.PortableSafeBlock;
import tk.sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.state.properties.DoubleBlockHalf.LOWER;

public class LootTables extends LootTableProvider {
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> tables = new ArrayList<>();

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        tables.clear();

        standardDropTable(Blocks.LOCKSMITH_TABLE);
        standardDropTable(Blocks.KEYSMITH_TABLE);

        lockedDoor(Blocks.LOCKED_OAK_DOOR);
        lockedDoor(Blocks.LOCKED_BIRCH_DOOR);
        lockedDoor(Blocks.LOCKED_SPRUCE_DOOR);
        lockedDoor(Blocks.LOCKED_JUNGLE_DOOR);
        lockedDoor(Blocks.LOCKED_ACACIA_DOOR);
        lockedDoor(Blocks.LOCKED_DARK_OAK_DOOR);
        lockedDoor(Blocks.LOCKED_CRIMSON_DOOR);
        lockedDoor(Blocks.LOCKED_WARPED_DOOR);
        lockedDoor(Blocks.LOCKED_IRON_DOOR);

        padlockedDoor(Blocks.PADLOCKED_OAK_DOOR);
        padlockedDoor(Blocks.PADLOCKED_BIRCH_DOOR);
        padlockedDoor(Blocks.PADLOCKED_SPRUCE_DOOR);
        padlockedDoor(Blocks.PADLOCKED_JUNGLE_DOOR);
        padlockedDoor(Blocks.PADLOCKED_ACACIA_DOOR);
        padlockedDoor(Blocks.PADLOCKED_DARK_OAK_DOOR);
        padlockedDoor(Blocks.PADLOCKED_CRIMSON_DOOR);
        padlockedDoor(Blocks.PADLOCKED_WARPED_DOOR);
        padlockedDoor(Blocks.PADLOCKED_IRON_DOOR);

        portableSafe();

        return tables;
    }

    void lockedDoor(LockedDoorBlock block) {
        StatePropertiesPredicate.Builder predicate = StatePropertiesPredicate.Builder.newBuilder()
                .withProp(LockedDoorBlock.HALF, LOWER);

        LootPool.Builder builder = createStandardDrops(block.baseBlock);
        builder.acceptCondition(BlockStateProperty.builder(block).fromProperties(predicate));

        blockTable(block, LootTable.builder().addLootPool(builder));
    }

    void padlockedDoor(PadlockedDoorBlock block) {
        StatePropertiesPredicate.Builder predicate = StatePropertiesPredicate.Builder.newBuilder()
                .withProp(PadlockedDoorBlock.HALF, LOWER);

        LootPool.Builder doorItem = createStandardDrops(block.baseBlock);
        doorItem.acceptCondition(BlockStateProperty.builder(block).fromProperties(predicate));

        LootPool.Builder padlock = createStandardDrops(Items.BROKEN_LOCK_PIECES);
        padlock.acceptCondition(BlockStateProperty.builder(block).fromProperties(predicate));

        blockTable(block, LootTable.builder().addLootPool(doorItem).addLootPool(padlock));
    }

    void portableSafe() {
        LootFunction.Builder<?> copyNameFunc = CopyName.builder(CopyName.Source.BLOCK_ENTITY);
        SetContents.Builder contentsFunc = SetContents.builderIn()
                .addLootEntry(DynamicLootEntry.func_216162_a(PortableSafeBlock.CONTENTS));
        CopyNbt.Builder copyLockFunc = createCopyLockFunc(PortableSafeTileEntity.TAG_LOCK_ITEM);

        LootPool.Builder safeItem = createStandardDrops(Blocks.PORTABLE_SAFE);
        safeItem.acceptFunction(copyNameFunc).acceptFunction(contentsFunc).acceptFunction(copyLockFunc);

        blockTable(Blocks.PORTABLE_SAFE, LootTable.builder().addLootPool(safeItem));
    }

    CopyNbt.Builder createCopyLockFunc(String tag) {
        return CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
                .addOperation(tag, "BlockEntityTag." + tag, CopyNbt.Action.REPLACE);
    }

    void standardDropTable(Block b) {
        blockTable(b, LootTable.builder().addLootPool(createStandardDrops(b)));
    }

    void blockTable(Block b, LootTable.Builder lootTable) {
        addTable(b.getLootTable(), lootTable, LootParameterSets.BLOCK);
    }

    void addTable(ResourceLocation path, LootTable.Builder lootTable, LootParameterSet paramSet) {
        tables.add(Pair.of(() -> (lootBuilder) -> lootBuilder.accept(path, lootTable), paramSet));
    }

    LootPool.Builder createStandardDrops(IItemProvider itemProvider) {
        return LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(SurvivesExplosion.builder())
                .addEntry(ItemLootEntry.builder(itemProvider));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        map.forEach((loc, table) -> LootTableManager.validateLootTable(validationtracker, loc, table));
    }
}
