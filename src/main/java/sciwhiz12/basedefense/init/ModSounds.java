package sciwhiz12.basedefense.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import sciwhiz12.basedefense.BaseDefense;

@ObjectHolder(BaseDefense.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModSounds {

    public static final SoundEvent LOCKED_DOOR_ATTEMPT = null;
    public static final SoundEvent LOCKED_DOOR_RELOCK = null;
    public static final SoundEvent LOCKED_DOOR_UNLOCK = null;

    @SubscribeEvent
    public static void onRegister(RegistryEvent.Register<SoundEvent> event) {
        BaseDefense.LOG.debug("Registering sounds");
        final IForgeRegistry<SoundEvent> reg = event.getRegistry();

        reg.register(new SoundEvent(makeLocation("locked_door.attempt")).setRegistryName("locked_door_attempt"));
        reg.register(new SoundEvent(makeLocation("locked_door.relock")).setRegistryName("locked_door_relock"));
        reg.register(new SoundEvent(makeLocation("locked_door.unlock")).setRegistryName("locked_door_unlock"));
    }

    private static ResourceLocation makeLocation(String path) {
        return new ResourceLocation(BaseDefense.MODID, path);
    }
}
