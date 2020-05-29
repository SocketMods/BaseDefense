package sciwhiz12.basedefense.init;

import javax.annotation.Nonnull;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.item.key.KeyItem;
import sciwhiz12.basedefense.item.key.SkeletonKeyItem;
import sciwhiz12.basedefense.item.lock.BrokenPadlockItem;
import sciwhiz12.basedefense.item.lock.LockCoreItem;
import sciwhiz12.basedefense.item.lock.PadlockItem;

public class ModItems {
    public static final ItemGroup GROUP = new ItemGroup(BaseDefense.MODID) {
        @Override
        @Nonnull
        public ItemStack createIcon() {
            return new ItemStack(ModItems.LOCK_CORE.get());
        }
    };

    public static final DeferredRegister<Item> REGISTER = new DeferredRegister<>(ForgeRegistries.ITEMS, BaseDefense.MODID);

    // Regular Items
    public static final RegistryObject<Item> BLANK_KEY = REGISTER.register(
        "blank_key", () -> new Item(new Item.Properties().group(GROUP))
    );
    public static final RegistryObject<Item> KEY = REGISTER.register("key", () -> new KeyItem());
    public static final RegistryObject<Item> SKELETON_KEY = REGISTER.register("skeleton_key", () -> new SkeletonKeyItem());

    public static final RegistryObject<Item> LOCK_CORE = REGISTER.register("lock_core", () -> new LockCoreItem());
    public static final RegistryObject<Item> PADLOCK = REGISTER.register("padlock", () -> new PadlockItem());
    public static final RegistryObject<Item> BROKEN_PADLOCK = REGISTER.register(
        "broken_padlock", () -> new BrokenPadlockItem()
    );

    // BlockItems
    public static final RegistryObject<Item> TEST_LOCK_BLOCK_ITEM = REGISTER.register(
        "test_lock_block", () -> new BlockItem(ModBlocks.TEST_LOCK_BLOCK.get(), new Item.Properties().group(GROUP))
    );
    public static final RegistryObject<Item> KEYSMITH_BLOCK_ITEM = REGISTER.register(
        "keysmith_table", () -> new BlockItem(ModBlocks.KEYSMITH_BLOCK.get(), new Item.Properties().group(GROUP))
    );
    public static final RegistryObject<Item> LOCKSMITH_BLOCK_ITEM = REGISTER.register(
        "locksmith_table", () -> new BlockItem(ModBlocks.LOCKSMITH_BLOCK.get(), new Item.Properties().group(GROUP))
    );
    private static final Item.Properties door_lock_props = new Item.Properties().group(GROUP).maxDamage(0);
    public static final RegistryObject<Item> LOCKED_IRON_DOOR_ITEM = REGISTER.register(
        "locked_iron_door", () -> new BlockItem(ModBlocks.LOCKED_IRON_DOOR.get(), door_lock_props)
    );
    public static final RegistryObject<Item> LOCKED_OAK_DOOR_ITEM = REGISTER.register(
        "locked_oak_door", () -> new BlockItem(ModBlocks.LOCKED_OAK_DOOR.get(), door_lock_props)
    );
    public static final RegistryObject<Item> LOCKED_BIRCH_DOOR_ITEM = REGISTER.register(
        "locked_birch_door", () -> new BlockItem(ModBlocks.LOCKED_BIRCH_DOOR.get(), door_lock_props)
    );
    public static final RegistryObject<Item> LOCKED_SPRUCE_DOOR_ITEM = REGISTER.register(
        "locked_spruce_door", () -> new BlockItem(ModBlocks.LOCKED_SPRUCE_DOOR.get(), door_lock_props)
    );
    public static final RegistryObject<Item> LOCKED_JUNGLE_DOOR_ITEM = REGISTER.register(
        "locked_jungle_door", () -> new BlockItem(ModBlocks.LOCKED_JUNGLE_DOOR.get(), door_lock_props)
    );
    public static final RegistryObject<Item> LOCKED_ACACIA_DOOR_ITEM = REGISTER.register(
        "locked_acacia_door", () -> new BlockItem(ModBlocks.LOCKED_ACACIA_DOOR.get(), door_lock_props)
    );
    public static final RegistryObject<Item> LOCKED_DARK_OAK_DOOR_ITEM = REGISTER.register(
        "locked_dark_oak_door", () -> new BlockItem(ModBlocks.LOCKED_DARK_OAK_DOOR.get(), door_lock_props)
    );
}
