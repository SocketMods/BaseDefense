package sciwhiz12.basedefense.init;

import static sciwhiz12.basedefense.BaseDefense.*;
import static sciwhiz12.basedefense.util.Util.Null;

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

    public static final SoundEvent LOCKED_DOOR_ATTEMPT = Null();
    public static final SoundEvent LOCKED_DOOR_RELOCK = Null();
    public static final SoundEvent LOCKED_DOOR_UNLOCK = Null();

    @SubscribeEvent
    static void onRegister(RegistryEvent.Register<SoundEvent> event) {
        LOG.debug(COMMON, "Registering sound events");
        final IForgeRegistry<SoundEvent> reg = event.getRegistry();

        reg.register(new SoundEvent(modLoc("locked_door.attempt")).setRegistryName("locked_door_attempt"));
        reg.register(new SoundEvent(modLoc("locked_door.relock")).setRegistryName("locked_door_relock"));
        reg.register(new SoundEvent(modLoc("locked_door.unlock")).setRegistryName("locked_door_unlock"));
    }
}
