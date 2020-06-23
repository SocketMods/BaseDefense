package sciwhiz12.basedefense.init;

import static sciwhiz12.basedefense.BaseDefense.COMMON;
import static sciwhiz12.basedefense.BaseDefense.LOG;
import static sciwhiz12.basedefense.util.Util.Null;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.item.LockedDoorBlockItem;
import sciwhiz12.basedefense.item.key.KeyItem;
import sciwhiz12.basedefense.item.key.KeyringItem;
import sciwhiz12.basedefense.item.key.SkeletonKeyItem;
import sciwhiz12.basedefense.item.lock.BrokenPadlockItem;
import sciwhiz12.basedefense.item.lock.LockCoreItem;
import sciwhiz12.basedefense.item.lock.PadlockItem;

@ObjectHolder(BaseDefense.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModItems {
    public static final Item BLANK_KEY = Null();
    public static final KeyItem KEY = Null();
    public static final SkeletonKeyItem SKELETON_KEY = Null();
    public static final LockCoreItem LOCK_CORE = Null();
    public static final PadlockItem PADLOCK = Null();
    public static final BrokenPadlockItem BROKEN_PADLOCK = Null();
    public static final KeyringItem KEYRING = Null();

    public static final BlockItem TEST_LOCK_BLOCK = Null();
    public static final BlockItem KEYSMITH_TABLE = Null();
    public static final BlockItem LOCKSMITH_TABLE = Null();

    public static final BlockItem LOCKED_IRON_DOOR = Null();
    public static final BlockItem LOCKED_OAK_DOOR = Null();
    public static final BlockItem LOCKED_BIRCH_DOOR = Null();
    public static final BlockItem LOCKED_SPRUCE_DOOR = Null();
    public static final BlockItem LOCKED_JUNGLE_DOOR = Null();
    public static final BlockItem LOCKED_ACACIA_DOOR = Null();
    public static final BlockItem LOCKED_DARK_OAK_DOOR = Null();

    public static ItemGroup GROUP = new ItemGroup(BaseDefense.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.LOCK_CORE);
        }
    };

    @SubscribeEvent
    static void onRegister(RegistryEvent.Register<Item> event) {
        LOG.debug(COMMON, "Registering items");
        final IForgeRegistry<Item> reg = event.getRegistry();

        final Item.Properties defaultProps = new Item.Properties().group(GROUP);

        reg.register(new Item(defaultProps).setRegistryName("blank_key"));
        reg.register(new KeyItem().setRegistryName("key"));
        reg.register(new SkeletonKeyItem().setRegistryName("skeleton_key"));
        reg.register(new LockCoreItem().setRegistryName("lock_core"));
        reg.register(new PadlockItem().setRegistryName("padlock"));
        reg.register(new BrokenPadlockItem().setRegistryName("broken_padlock"));
        reg.register(new KeyringItem().setRegistryName("keyring"));

        reg.register(new BlockItem(ModBlocks.TEST_LOCK_BLOCK, defaultProps).setRegistryName("test_lock_block"));
        reg.register(new BlockItem(ModBlocks.KEYSMITH_TABLE, defaultProps).setRegistryName("keysmith_table"));
        reg.register(new BlockItem(ModBlocks.LOCKSMITH_TABLE, defaultProps).setRegistryName("locksmith_table"));

        reg.register(new LockedDoorBlockItem(ModBlocks.LOCKED_OAK_DOOR).setRegistryName("locked_oak_door"));
        reg.register(new LockedDoorBlockItem(ModBlocks.LOCKED_BIRCH_DOOR).setRegistryName("locked_birch_door"));
        reg.register(new LockedDoorBlockItem(ModBlocks.LOCKED_SPRUCE_DOOR).setRegistryName("locked_spruce_door"));
        reg.register(new LockedDoorBlockItem(ModBlocks.LOCKED_JUNGLE_DOOR).setRegistryName("locked_jungle_door"));
        reg.register(new LockedDoorBlockItem(ModBlocks.LOCKED_ACACIA_DOOR).setRegistryName("locked_acacia_door"));
        reg.register(new LockedDoorBlockItem(ModBlocks.LOCKED_DARK_OAK_DOOR).setRegistryName("locked_dark_oak_door"));
        reg.register(new LockedDoorBlockItem(ModBlocks.LOCKED_IRON_DOOR).setRegistryName("locked_iron_door"));
    }
}
