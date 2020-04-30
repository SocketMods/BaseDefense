package sciwhiz12.basedefense.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.item.lock.BlankKeyItem;
import sciwhiz12.basedefense.item.lock.KeyItem;

public class BDItems {
    public static final DeferredRegister<Item> REGISTER = new DeferredRegister<>(
            ForgeRegistries.ITEMS, BaseDefense.MODID
    );

    public static final RegistryObject<Item> BLANK_KEY = REGISTER.register(
            "blank_key", () -> new BlankKeyItem()
    );
    
    public static final RegistryObject<Item> KEY = REGISTER.register(
            "key", () -> new KeyItem()
    );
}
