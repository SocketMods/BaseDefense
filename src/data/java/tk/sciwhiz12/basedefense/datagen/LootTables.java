package tk.sciwhiz12.basedefense.datagen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import tk.sciwhiz12.basedefense.Reference;
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

import static net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER;

public class LootTables extends LootTableProvider {
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables = new ArrayList<>();

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
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
        StatePropertiesPredicate.Builder predicate = StatePropertiesPredicate.Builder.properties()
            .hasProperty(LockedDoorBlock.HALF, LOWER);

        LootPool.Builder builder = createStandardDrops(block.baseBlock);
        builder.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(predicate));

        blockTable(block, LootTable.lootTable().withPool(builder));
    }

    void padlockedDoor(PadlockedDoorBlock block) {
        StatePropertiesPredicate.Builder predicate = StatePropertiesPredicate.Builder.properties()
            .hasProperty(PadlockedDoorBlock.HALF, LOWER);

        LootPool.Builder doorItem = createStandardDrops(block.baseBlock);
        doorItem.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(predicate));

        LootPool.Builder padlock = createStandardDrops(Items.BROKEN_LOCK_PIECES);
        padlock.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(predicate));

        blockTable(block, LootTable.lootTable().withPool(doorItem).withPool(padlock));
    }

    void portableSafe() {
        LootItemConditionalFunction.Builder<?> copyNameFunc = CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY);
        SetContainerContents.Builder contentsFunc = SetContainerContents.setContents(Reference.TileEntities.PORTABLE_SAFE)
            .withEntry(DynamicLoot.dynamicEntry(PortableSafeBlock.CONTENTS));
        CopyNbtFunction.Builder copyLockFunc = createCopyLockFunc(PortableSafeTileEntity.TAG_LOCK_ITEM);

        LootPool.Builder safeItem = createStandardDrops(Blocks.PORTABLE_SAFE);
        safeItem.apply(copyNameFunc).apply(contentsFunc).apply(copyLockFunc);

        blockTable(Blocks.PORTABLE_SAFE, LootTable.lootTable().withPool(safeItem));
    }

    CopyNbtFunction.Builder createCopyLockFunc(String tag) {
        return CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
            .copy(tag, "BlockEntityTag." + tag, CopyNbtFunction.MergeStrategy.REPLACE);
    }

    void standardDropTable(Block b) {
        blockTable(b, LootTable.lootTable().withPool(createStandardDrops(b)));
    }

    void blockTable(Block b, LootTable.Builder lootTable) {
        addTable(b.getLootTable(), lootTable, LootContextParamSets.BLOCK);
    }

    void addTable(ResourceLocation path, LootTable.Builder lootTable, LootContextParamSet paramSet) {
        tables.add(Pair.of(() -> (lootBuilder) -> lootBuilder.accept(path, lootTable), paramSet));
    }

    LootPool.Builder createStandardDrops(ItemLike itemProvider) {
        return LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(ExplosionCondition.survivesExplosion())
            .add(LootItem.lootTableItem(itemProvider));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        map.forEach((loc, table) -> net.minecraft.world.level.storage.loot.LootTables.validate(validationtracker, loc, table));
    }
}
