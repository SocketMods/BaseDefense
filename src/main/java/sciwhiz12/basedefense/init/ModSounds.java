package sciwhiz12.basedefense.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTER = new DeferredRegister<>(
        ForgeRegistries.SOUND_EVENTS, BaseDefense.MODID
    );

    public static final RegistryObject<SoundEvent> LOCKED_DOOR_ATTEMPT = REGISTER.register(
        "locked_door_attempt", () -> new SoundEvent(makeLocation("locked_door.attempt"))
    );
    public static final RegistryObject<SoundEvent> LOCKED_DOOR_RELOCK = REGISTER.register(
        "locked_door_relock", () -> new SoundEvent(makeLocation("locked_door.relock"))
    );
    public static final RegistryObject<SoundEvent> LOCKED_DOOR_UNLOCK = REGISTER.register(
        "locked_door_unlock", () -> new SoundEvent(makeLocation("locked_door.unlock"))
    );

    private static ResourceLocation makeLocation(String path) {
        return new ResourceLocation(BaseDefense.MODID, path);
    }
}
