package sciwhiz12.basedefense.init;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;
import sciwhiz12.basedefense.capabilities.CodedKey;
import sciwhiz12.basedefense.capabilities.CodedLock;

@EventBusSubscriber(bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModCapabilities {
    @CapabilityInject(ILock.class)
    public static Capability<ILock> LOCK;

    @CapabilityInject(IKey.class)
    public static Capability<IKey> KEY;

    @SubscribeEvent
    static void onCommonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ILock.class, new Storage<>(), CodedLock::new);
        CapabilityManager.INSTANCE.register(IKey.class, new Storage<>(), CodedKey::new);
    }

    public static class Storage<T extends INBTSerializable<INBT>> implements Capability.IStorage<T> {
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
            instance.deserializeNBT(nbt);
        }
    }
}
