package sciwhiz12.basedefense;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.item.lock.BlankKeyItem;
import sciwhiz12.basedefense.item.lock.BlankLockItem;
import sciwhiz12.basedefense.item.lock.KeyItem;
import sciwhiz12.basedefense.item.lock.LockItem;

public class BDItems {
    public static final DeferredRegister<Item> REGISTER = new DeferredRegister<>(
            ForgeRegistries.ITEMS, BaseDefense.MODID
    );

    public static final RegistryObject<Item> BLANK_KEY = REGISTER.register(
            "blank_key", () -> new BlankKeyItem()
    );

    public static final RegistryObject<Item> KEY = REGISTER.register("key", () -> new KeyItem());

    public static final RegistryObject<Item> BLANK_LOCK = REGISTER.register(
            "blank_lock", () -> new BlankLockItem()
    );

    public static final RegistryObject<Item> LOCK = REGISTER.register("lock", () -> new LockItem());

    public static final RegistryObject<Item> TEST_LOCK_BLOCK_ITEM = REGISTER.register(
            "test_lock_block", () -> new BlockItem(
                    BDBlocks.TEST_LOCK_BLOCK.get(), new Item.Properties()
            )
    );
}
