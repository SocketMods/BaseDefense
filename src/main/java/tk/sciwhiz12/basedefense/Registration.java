package tk.sciwhiz12.basedefense;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import tk.sciwhiz12.basedefense.api.capablities.ICodeHolder;
import tk.sciwhiz12.basedefense.api.capablities.IContainsCode;
import tk.sciwhiz12.basedefense.api.capablities.IKey;
import tk.sciwhiz12.basedefense.api.capablities.ILock;
import tk.sciwhiz12.basedefense.block.KeysmithBlock;
import tk.sciwhiz12.basedefense.block.LockedDoorBlock;
import tk.sciwhiz12.basedefense.block.LocksmithBlock;
import tk.sciwhiz12.basedefense.block.PadlockedDoorBlock;
import tk.sciwhiz12.basedefense.block.PortableSafeBlock;
import tk.sciwhiz12.basedefense.container.KeyringContainer;
import tk.sciwhiz12.basedefense.container.KeysmithContainer;
import tk.sciwhiz12.basedefense.container.LocksmithContainer;
import tk.sciwhiz12.basedefense.container.PortableSafeContainer;
import tk.sciwhiz12.basedefense.item.BrokenLockPiecesItem;
import tk.sciwhiz12.basedefense.item.LockedBlockItem;
import tk.sciwhiz12.basedefense.item.PortableSafeBlockItem;
import tk.sciwhiz12.basedefense.item.key.AdminKeyItem;
import tk.sciwhiz12.basedefense.item.key.KeyItem;
import tk.sciwhiz12.basedefense.item.key.KeyringItem;
import tk.sciwhiz12.basedefense.item.lock.AdminLockCoreItem;
import tk.sciwhiz12.basedefense.item.lock.AdminPadlockItem;
import tk.sciwhiz12.basedefense.item.lock.CodedLockCoreItem;
import tk.sciwhiz12.basedefense.item.lock.CodedPadlockItem;
import tk.sciwhiz12.basedefense.recipe.CodedLockRecipe;
import tk.sciwhiz12.basedefense.recipe.ColoringRecipe;
import tk.sciwhiz12.basedefense.recipe.LockedItemIngredient;
import tk.sciwhiz12.basedefense.recipe.LockedItemRecipe;
import tk.sciwhiz12.basedefense.tileentity.LockableTile;
import tk.sciwhiz12.basedefense.tileentity.LockedDoorTile;
import tk.sciwhiz12.basedefense.tileentity.PadlockedDoorTile;
import tk.sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;
import tk.sciwhiz12.basedefense.util.RecipeHelper;
import tk.sciwhiz12.basedefense.util.Util;

/**
 * Main class for registering objects of this mod.
 *
 * @author SciWhiz12
 */
@Mod.EventBusSubscriber(bus = Bus.MOD, modid = Reference.MODID)
public final class Registration {
    // Prevent instantiation
    private Registration() {}

    @SubscribeEvent
    static void registerBlocks(RegistryEvent.Register<Block> event) {
        BaseDefense.LOG.debug(BaseDefense.COMMON, "Registering blocks");
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

        reg.register(new PortableSafeBlock().setRegistryName("portable_safe"));
    }

    @SubscribeEvent
    static void registerCapabilities(RegisterCapabilitiesEvent event) {
        BaseDefense.LOG.debug(BaseDefense.COMMON, "Registering capabilities");
        event.register(ILock.class);
        event.register(IKey.class);
        event.register(IContainsCode.class);
        event.register(ICodeHolder.class);
    }

    @SubscribeEvent
    static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        BaseDefense.LOG.debug(BaseDefense.COMMON, "Registering containers");
        final IForgeRegistry<MenuType<?>> reg = event.getRegistry();

        reg.register(new MenuType<>(KeysmithContainer::new).setRegistryName("keysmith_table"));
        reg.register(new MenuType<>(LocksmithContainer::new).setRegistryName("locksmith_table"));
        reg.register(IForgeMenuType.create(KeyringContainer::new).setRegistryName("keyring"));
        reg.register(new MenuType<>(PortableSafeContainer::new).setRegistryName("portable_safe"));
    }

    @SubscribeEvent
    static void registerItems(RegistryEvent.Register<Item> event) {
        BaseDefense.LOG.debug(BaseDefense.COMMON, "Registering items");
        final IForgeRegistry<Item> reg = event.getRegistry();

        final Item.Properties defaultProps = new Item.Properties().tab(Reference.ITEM_GROUP);

        reg.register(new Item(defaultProps).setRegistryName("blank_key"));
        reg.register(new KeyItem().setRegistryName("key"));
        reg.register(new CodedLockCoreItem().setRegistryName("lock_core"));
        reg.register(new CodedPadlockItem().setRegistryName("padlock"));
        reg.register(new KeyringItem().setRegistryName("keyring"));
        reg.register(new BrokenLockPiecesItem().setRegistryName("broken_lock_pieces"));
        reg.register(new AdminKeyItem().setRegistryName("admin_key"));
        reg.register(new AdminLockCoreItem().setRegistryName("admin_lock_core"));
        reg.register(new AdminPadlockItem().setRegistryName("admin_padlock"));

        reg.register(new BlockItem(Reference.Blocks.KEYSMITH_TABLE, defaultProps).setRegistryName("keysmith_table"));
        reg.register(new BlockItem(Reference.Blocks.LOCKSMITH_TABLE, defaultProps).setRegistryName("locksmith_table"));

        reg.register(new LockedBlockItem(Reference.Blocks.LOCKED_OAK_DOOR).setRegistryName("locked_oak_door"));
        reg.register(new LockedBlockItem(Reference.Blocks.LOCKED_BIRCH_DOOR).setRegistryName("locked_birch_door"));
        reg.register(new LockedBlockItem(Reference.Blocks.LOCKED_SPRUCE_DOOR).setRegistryName("locked_spruce_door"));
        reg.register(new LockedBlockItem(Reference.Blocks.LOCKED_JUNGLE_DOOR).setRegistryName("locked_jungle_door"));
        reg.register(new LockedBlockItem(Reference.Blocks.LOCKED_ACACIA_DOOR).setRegistryName("locked_acacia_door"));
        reg.register(new LockedBlockItem(Reference.Blocks.LOCKED_DARK_OAK_DOOR).setRegistryName("locked_dark_oak_door"));
        reg.register(new LockedBlockItem(Reference.Blocks.LOCKED_CRIMSON_DOOR).setRegistryName("locked_crimson_door"));
        reg.register(new LockedBlockItem(Reference.Blocks.LOCKED_WARPED_DOOR).setRegistryName("locked_warped_door"));
        reg.register(new LockedBlockItem(Reference.Blocks.LOCKED_IRON_DOOR).setRegistryName("locked_iron_door"));

        reg.register(new PortableSafeBlockItem(Reference.Blocks.PORTABLE_SAFE, new Item.Properties().tab(Reference.ITEM_GROUP).durability(0))
                .setRegistryName("portable_safe"));
    }

    @SubscribeEvent
    static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        BaseDefense.LOG.debug(BaseDefense.COMMON, "Registering recipe and ingredient serializers");
        final IForgeRegistry<RecipeSerializer<?>> reg = event.getRegistry();

        reg.register(new RecipeHelper.ShapedSerializer<>(LockedItemRecipe::new).setRegistryName("locked_item"));
        reg.register(new RecipeHelper.ShapedSerializer<>(CodedLockRecipe::new).setRegistryName("coded_lock"));
        reg.register(new SimpleRecipeSerializer<>(ColoringRecipe::new).setRegistryName("coloring"));

        Reference.IngredientSerializers.LOCKED_ITEM = CraftingHelper
                .register(Reference.modLoc("locked_item"), new LockedItemIngredient.Serializer());
    }

    @SubscribeEvent
    static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
        BaseDefense.LOG.debug(BaseDefense.COMMON, "Registering sound events");
        final IForgeRegistry<SoundEvent> reg = event.getRegistry();

        reg.register(new SoundEvent(Reference.modLoc("locked_door.attempt")).setRegistryName("locked_door_attempt"));
        reg.register(new SoundEvent(Reference.modLoc("locked_door.relock")).setRegistryName("locked_door_relock"));
        reg.register(new SoundEvent(Reference.modLoc("locked_door.unlock")).setRegistryName("locked_door_unlock"));
    }

    @SubscribeEvent
    static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        BaseDefense.LOG.debug(BaseDefense.COMMON, "Registering tile entities");
        final IForgeRegistry<BlockEntityType<?>> reg = event.getRegistry();

        reg.register(makeType(LockableTile::new).setRegistryName("lockable_tile"));
        reg.register(makeType(PadlockedDoorTile::new, Reference.Blocks.PADLOCKED_IRON_DOOR, Reference.Blocks.PADLOCKED_OAK_DOOR, Reference.Blocks.PADLOCKED_BIRCH_DOOR,
                Reference.Blocks.PADLOCKED_SPRUCE_DOOR, Reference.Blocks.PADLOCKED_JUNGLE_DOOR, Reference.Blocks.PADLOCKED_ACACIA_DOOR, Reference.Blocks.PADLOCKED_DARK_OAK_DOOR,
                Reference.Blocks.PADLOCKED_CRIMSON_DOOR, Reference.Blocks.PADLOCKED_WARPED_DOOR)
                .setRegistryName("padlocked_door"));
        reg.register(makeType(LockedDoorTile::new, Reference.Blocks.LOCKED_IRON_DOOR, Reference.Blocks.LOCKED_OAK_DOOR, Reference.Blocks.LOCKED_BIRCH_DOOR, Reference.Blocks.LOCKED_SPRUCE_DOOR,
                Reference.Blocks.LOCKED_JUNGLE_DOOR, Reference.Blocks.LOCKED_ACACIA_DOOR, Reference.Blocks.LOCKED_DARK_OAK_DOOR, Reference.Blocks.LOCKED_CRIMSON_DOOR, Reference.Blocks.LOCKED_WARPED_DOOR)
                .setRegistryName("locked_door"));
        reg.register(makeType(PortableSafeTileEntity::new, Reference.Blocks.PORTABLE_SAFE).setRegistryName("portable_safe"));
    }

    private static <T extends BlockEntity> BlockEntityType<T> makeType(BlockEntityType.BlockEntitySupplier<T> factory, Block... validBlocks) {
        return BlockEntityType.Builder.of(factory, validBlocks).build(Util.Null());
    }
}
