package sciwhiz12.basedefense.init;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.container.KeysmithContainer;
import sciwhiz12.basedefense.container.LocksmithContainer;

public class ModContainers {
    public static final DeferredRegister<ContainerType<?>> REGISTER = new DeferredRegister<>(
        ForgeRegistries.CONTAINERS, BaseDefense.MODID
    );

    public static final RegistryObject<ContainerType<KeysmithContainer>> KEYSMITH_CONTAINER = REGISTER
        .register("keysmith_table", () -> new ContainerType<>(KeysmithContainer::new));

    public static final RegistryObject<ContainerType<LocksmithContainer>> LOCKSMITH_CONTAINER = REGISTER
        .register("locksmith_table", () -> new ContainerType<>(LocksmithContainer::new));
}
