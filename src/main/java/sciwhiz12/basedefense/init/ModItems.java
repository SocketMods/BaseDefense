package sciwhiz12.basedefense.init;

import static sciwhiz12.basedefense.util.Util.Null;

import net.minecraft.block.Block;
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
import sciwhiz12.basedefense.item.key.KeyItem;
import sciwhiz12.basedefense.item.key.KeyringItem;
import sciwhiz12.basedefense.item.key.SkeletonKeyItem;
import sciwhiz12.basedefense.item.lock.BrokenPadlockItem;
import sciwhiz12.basedefense.item.lock.LockCoreItem;
import sciwhiz12.basedefense.item.lock.PadlockItem;

import java.security.Key;

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
        BaseDefense.LOG.debug("Registering items");
        final IForgeRegistry<Item> reg = event.getRegistry();

        final Item.Properties default_props = new Item.Properties().group(GROUP);
        reg.register(new Item(default_props).setRegistryName("blank_key"));
        reg.register(new KeyItem().setRegistryName("key"));
        reg.register(new SkeletonKeyItem().setRegistryName("skeleton_key"));
        reg.register(new LockCoreItem().setRegistryName("lock_core"));
        reg.register(new PadlockItem().setRegistryName("padlock"));
        reg.register(new BrokenPadlockItem().setRegistryName("broken_padlock"));
        reg.register(new KeyringItem().setRegistryName("keyring"));

        final Block[] blocks = { ModBlocks.TEST_LOCK_BLOCK, ModBlocks.KEYSMITH_TABLE, ModBlocks.LOCKSMITH_TABLE };
        for (Block b : blocks) { reg.register(new BlockItem(b, default_props).setRegistryName(b.getRegistryName())); }

        final Item.Properties zero_damage_props = new Item.Properties().group(GROUP).maxDamage(0);
        final Block[] doors = { ModBlocks.LOCKED_IRON_DOOR, ModBlocks.LOCKED_OAK_DOOR, ModBlocks.LOCKED_BIRCH_DOOR,
                ModBlocks.LOCKED_SPRUCE_DOOR, ModBlocks.LOCKED_JUNGLE_DOOR, ModBlocks.LOCKED_ACACIA_DOOR,
                ModBlocks.LOCKED_DARK_OAK_DOOR };
        for (Block b : doors) { reg.register(new BlockItem(b, zero_damage_props).setRegistryName(b.getRegistryName())); }
    }
}
