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
import sciwhiz12.basedefense.item.lock.KeyItem;
import sciwhiz12.basedefense.item.lock.LockItem;

public class BDItems {
    public static final ItemGroup GROUP = new ItemGroup(BaseDefense.MODID) {
        @Override
        @Nonnull
        public ItemStack createIcon() {
            return new ItemStack(BDItems.LOCK.get());
        }
    };
    public static final DeferredRegister<Item> ITEM = new DeferredRegister<>(
            ForgeRegistries.ITEMS, BaseDefense.MODID
    );

    public static final RegistryObject<Item> BLANK_KEY = ITEM.register(
            "blank_key", () -> new Item(new Item.Properties().group(GROUP))
    );
    public static final RegistryObject<Item> KEY = ITEM.register("key", () -> new KeyItem());

    public static final RegistryObject<Item> BLANK_LOCK = ITEM.register(
            "blank_lock", () -> new Item(new Item.Properties().group(GROUP))
    );
    public static final RegistryObject<Item> LOCK = ITEM.register("lock", () -> new LockItem());

    public static final RegistryObject<Item> TEST_LOCK_BLOCK_ITEM = ITEM.register(
            "test_lock_block", () -> new BlockItem(
                    BDBlocks.TEST_LOCK_BLOCK.get(), new Item.Properties().group(GROUP)
            )
    );

    public static final RegistryObject<Item> KEYSMITH_BLOCK_ITEM = ITEM.register(
            "keysmith_table", () -> new BlockItem(
                    BDBlocks.KEYSMITH_BLOCK.get(), new Item.Properties().group(GROUP)
            )
    );
    public static final RegistryObject<Item> LOCKSMITH_BLOCK_ITEM = ITEM.register(
            "locksmith_table", () -> new BlockItem(
                    BDBlocks.LOCKSMITH_BLOCK.get(), new Item.Properties().group(GROUP)
            )
    );
}
