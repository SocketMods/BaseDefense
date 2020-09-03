package sciwhiz12.basedefense;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;
import sciwhiz12.basedefense.api.capablities.IContainsCode;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;
import sciwhiz12.basedefense.block.KeysmithBlock;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.block.LocksmithBlock;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.capabilities.CodeHolder;
import sciwhiz12.basedefense.capabilities.CodedKey;
import sciwhiz12.basedefense.capabilities.CodedLock;
import sciwhiz12.basedefense.capabilities.FlexibleStorage;
import sciwhiz12.basedefense.container.KeyringContainer;
import sciwhiz12.basedefense.container.KeysmithContainer;
import sciwhiz12.basedefense.container.LocksmithContainer;
import sciwhiz12.basedefense.item.BrokenLockPiecesItem;
import sciwhiz12.basedefense.item.LockedDoorBlockItem;
import sciwhiz12.basedefense.item.key.AdminKeyItem;
import sciwhiz12.basedefense.item.key.KeyItem;
import sciwhiz12.basedefense.item.key.KeyringItem;
import sciwhiz12.basedefense.item.lock.AdminLockCoreItem;
import sciwhiz12.basedefense.item.lock.AdminPadlockItem;
import sciwhiz12.basedefense.item.lock.CodedLockCoreItem;
import sciwhiz12.basedefense.item.lock.CodedPadlockItem;
import sciwhiz12.basedefense.recipe.CodedLockRecipe;
import sciwhiz12.basedefense.recipe.ColoringRecipe;
import sciwhiz12.basedefense.recipe.LockedItemIngredient;
import sciwhiz12.basedefense.recipe.LockedItemRecipe;
import sciwhiz12.basedefense.tileentity.LockableTile;
import sciwhiz12.basedefense.tileentity.LockedDoorTile;
import sciwhiz12.basedefense.tileentity.PadlockedDoorTile;
import sciwhiz12.basedefense.util.RecipeHelper;

import java.util.function.Supplier;

import static sciwhiz12.basedefense.BaseDefense.COMMON;
import static sciwhiz12.basedefense.BaseDefense.LOG;
import static sciwhiz12.basedefense.Reference.Blocks.*;
import static sciwhiz12.basedefense.Reference.*;
import static sciwhiz12.basedefense.util.Util.Null;

/**
 * Main class for registering objects of this mod.
 *
 * @author SciWhiz12
 */
@Mod.EventBusSubscriber(bus = Bus.MOD, modid = MODID)
public final class Registration {
    // Prevent instantiation
    private Registration() {}

    @SubscribeEvent
    static void registerBlocks(RegistryEvent.Register<Block> event) {
        LOG.debug(COMMON, "Registering blocks");
        final IForgeRegistry<Block> reg = event.getRegistry();

        reg.register(new KeysmithBlock().setRegistryName("keysmith_table"));
        reg.register(new LocksmithBlock().setRegistryName("locksmith_table"));

        PadlockedDoorBlock.clearReplacements();
        reg.register(new PadlockedDoorBlock(Blocks.OAK_DOOR).setRegistryName("padlocked_oak_door"));
        reg.register(new PadlockedDoorBlock(Blocks.BIRCH_DOOR).setRegistryName("padlocked_birch_door"));
        reg.register(new PadlockedDoorBlock(Blocks.SPRUCE_DOOR).setRegistryName("padlocked_spruce_door"));
        reg.register(new PadlockedDoorBlock(Blocks.JUNGLE_DOOR).setRegistryName("padlocked_jungle_door"));
        reg.register(new PadlockedDoorBlock(Blocks.ACACIA_DOOR).setRegistryName("padlocked_acacia_door"));
        reg.register(new PadlockedDoorBlock(Blocks.DARK_OAK_DOOR).setRegistryName("padlocked_dark_oak_door"));
        reg.register(new PadlockedDoorBlock(Blocks.CRIMSON_DOOR).setRegistryName("padlocked_crimson_door"));
        reg.register(new PadlockedDoorBlock(Blocks.WARPED_DOOR).setRegistryName("padlocked_warped_door"));
        reg.register(new PadlockedDoorBlock(Blocks.IRON_DOOR).setRegistryName("padlocked_iron_door"));

        reg.register(new LockedDoorBlock(Blocks.OAK_DOOR).setRegistryName("locked_oak_door"));
        reg.register(new LockedDoorBlock(Blocks.BIRCH_DOOR).setRegistryName("locked_birch_door"));
        reg.register(new LockedDoorBlock(Blocks.SPRUCE_DOOR).setRegistryName("locked_spruce_door"));
        reg.register(new LockedDoorBlock(Blocks.JUNGLE_DOOR).setRegistryName("locked_jungle_door"));
        reg.register(new LockedDoorBlock(Blocks.ACACIA_DOOR).setRegistryName("locked_acacia_door"));
        reg.register(new LockedDoorBlock(Blocks.DARK_OAK_DOOR).setRegistryName("locked_dark_oak_door"));
        reg.register(new LockedDoorBlock(Blocks.CRIMSON_DOOR).setRegistryName("locked_crimson_door"));
        reg.register(new LockedDoorBlock(Blocks.WARPED_DOOR).setRegistryName("locked_warped_door"));
        reg.register(new LockedDoorBlock(Blocks.IRON_DOOR).setRegistryName("locked_iron_door"));
    }

    @SubscribeEvent
    static void registerCapabilities(FMLCommonSetupEvent event) {
        LOG.debug(COMMON, "Registering capabilities");
        CapabilityManager.INSTANCE.register(ILock.class, new FlexibleStorage<>(), CodedLock::new);
        CapabilityManager.INSTANCE.register(IKey.class, new FlexibleStorage<>(), CodedKey::new);
        CapabilityManager.INSTANCE.register(IContainsCode.class, new FlexibleStorage<>(), CodeHolder::new);
        CapabilityManager.INSTANCE.register(ICodeHolder.class, new FlexibleStorage<>(), CodeHolder::new);
    }

    @SubscribeEvent
    static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        LOG.debug(COMMON, "Registering containers");
        final IForgeRegistry<ContainerType<?>> reg = event.getRegistry();

        reg.register(new ContainerType<>(KeysmithContainer::new).setRegistryName("keysmith_table"));
        reg.register(new ContainerType<>(LocksmithContainer::new).setRegistryName("locksmith_table"));
        reg.register(IForgeContainerType.create(KeyringContainer::new).setRegistryName("keyring"));
    }

    @SubscribeEvent
    static void registerItems(RegistryEvent.Register<Item> event) {
        LOG.debug(COMMON, "Registering items");
        final IForgeRegistry<Item> reg = event.getRegistry();

        final Item.Properties defaultProps = new Item.Properties().group(ITEM_GROUP);

        reg.register(new Item(defaultProps).setRegistryName("blank_key"));
        reg.register(new KeyItem().setRegistryName("key"));
        reg.register(new CodedLockCoreItem().setRegistryName("lock_core"));
        reg.register(new CodedPadlockItem().setRegistryName("padlock"));
        reg.register(new KeyringItem().setRegistryName("keyring"));
        reg.register(new BrokenLockPiecesItem().setRegistryName("broken_lock_pieces"));
        reg.register(new AdminKeyItem().setRegistryName("admin_key"));
        reg.register(new AdminLockCoreItem().setRegistryName("admin_lock_core"));
        reg.register(new AdminPadlockItem().setRegistryName("admin_padlock"));

        reg.register(new BlockItem(KEYSMITH_TABLE, defaultProps).setRegistryName("keysmith_table"));
        reg.register(new BlockItem(LOCKSMITH_TABLE, defaultProps).setRegistryName("locksmith_table"));

        reg.register(new LockedDoorBlockItem(LOCKED_OAK_DOOR).setRegistryName("locked_oak_door"));
        reg.register(new LockedDoorBlockItem(LOCKED_BIRCH_DOOR).setRegistryName("locked_birch_door"));
        reg.register(new LockedDoorBlockItem(LOCKED_SPRUCE_DOOR).setRegistryName("locked_spruce_door"));
        reg.register(new LockedDoorBlockItem(LOCKED_JUNGLE_DOOR).setRegistryName("locked_jungle_door"));
        reg.register(new LockedDoorBlockItem(LOCKED_ACACIA_DOOR).setRegistryName("locked_acacia_door"));
        reg.register(new LockedDoorBlockItem(LOCKED_DARK_OAK_DOOR).setRegistryName("locked_dark_oak_door"));
        reg.register(new LockedDoorBlockItem(LOCKED_CRIMSON_DOOR).setRegistryName("locked_crimson_door"));
        reg.register(new LockedDoorBlockItem(LOCKED_WARPED_DOOR).setRegistryName("locked_warped_door"));
        reg.register(new LockedDoorBlockItem(LOCKED_IRON_DOOR).setRegistryName("locked_iron_door"));
    }

    @SubscribeEvent
    static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        LOG.debug(COMMON, "Registering recipe and ingredient serializers");
        final IForgeRegistry<IRecipeSerializer<?>> reg = event.getRegistry();

        reg.register(new RecipeHelper.ShapedSerializer<>(LockedItemRecipe::new).setRegistryName("locked_item"));
        reg.register(new RecipeHelper.ShapedSerializer<>(CodedLockRecipe::new).setRegistryName("coded_lock"));
        reg.register(new SpecialRecipeSerializer<>(ColoringRecipe::new).setRegistryName("coloring"));

        IngredientSerializers.LOCKED_ITEM = CraftingHelper
                .register(modLoc("locked_item"), new LockedItemIngredient.Serializer());
    }

    @SubscribeEvent
    static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
        LOG.debug(COMMON, "Registering sound events");
        final IForgeRegistry<SoundEvent> reg = event.getRegistry();

        reg.register(new SoundEvent(modLoc("locked_door.attempt")).setRegistryName("locked_door_attempt"));
        reg.register(new SoundEvent(modLoc("locked_door.relock")).setRegistryName("locked_door_relock"));
        reg.register(new SoundEvent(modLoc("locked_door.unlock")).setRegistryName("locked_door_unlock"));
    }

    @SubscribeEvent
    static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        LOG.debug(COMMON, "Registering tile entities");
        final IForgeRegistry<TileEntityType<?>> reg = event.getRegistry();

        reg.register(makeType(LockableTile::new).setRegistryName("lockable_tile"));
        reg.register(makeType(PadlockedDoorTile::new, PADLOCKED_IRON_DOOR, PADLOCKED_OAK_DOOR, PADLOCKED_BIRCH_DOOR,
                PADLOCKED_SPRUCE_DOOR, PADLOCKED_JUNGLE_DOOR, PADLOCKED_ACACIA_DOOR, PADLOCKED_DARK_OAK_DOOR,
                PADLOCKED_CRIMSON_DOOR, PADLOCKED_WARPED_DOOR)
                .setRegistryName("padlocked_door"));
        reg.register(makeType(LockedDoorTile::new, LOCKED_IRON_DOOR, LOCKED_OAK_DOOR, LOCKED_BIRCH_DOOR, LOCKED_SPRUCE_DOOR,
                LOCKED_JUNGLE_DOOR, LOCKED_ACACIA_DOOR, LOCKED_DARK_OAK_DOOR, LOCKED_CRIMSON_DOOR, LOCKED_WARPED_DOOR)
                .setRegistryName("locked_door"));
    }

    private static <T extends TileEntity> TileEntityType<T> makeType(Supplier<T> factory, Block... validBlocks) {
        return TileEntityType.Builder.create(factory, validBlocks).build(Null());
    }
}
