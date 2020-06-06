package sciwhiz12.basedefense.init;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.container.KeyringContainer;
import sciwhiz12.basedefense.container.KeysmithContainer;
import sciwhiz12.basedefense.container.LocksmithContainer;

@ObjectHolder(BaseDefense.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModContainers {

    public static final ContainerType<KeysmithContainer> KEYSMITH_TABLE = null;
    public static final ContainerType<LocksmithContainer> LOCKSMITH_TABLE = null;
    public static final ContainerType<KeyringContainer> KEYRING = null;

    @SubscribeEvent
    static void onRegister(RegistryEvent.Register<ContainerType<?>> event) {
        BaseDefense.LOG.debug("Registering containers");
        final IForgeRegistry<ContainerType<?>> reg = event.getRegistry();

        reg.register(new ContainerType<>(KeysmithContainer::new).setRegistryName("keysmith_table"));
        reg.register(new ContainerType<>(LocksmithContainer::new).setRegistryName("locksmith_table"));
        reg.register(new ContainerType<>(KeyringContainer::new).setRegistryName("keyring"));
    }
}
