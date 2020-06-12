package sciwhiz12.basedefense.init;

import static sciwhiz12.basedefense.util.Util.Null;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.entity.PTZCameraEntity;

@ObjectHolder(BaseDefense.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModEntities {

    public static final EntityType<PTZCameraEntity> PTZ_CAMERA = Null();

    @SubscribeEvent
    static void onRegister(RegistryEvent.Register<EntityType<?>> event) {
        BaseDefense.LOG.debug("Registering entities");
        final IForgeRegistry<EntityType<?>> reg = event.getRegistry();

        reg.register(makeType("ptz_camera", PTZCameraEntity::new, EntityClassification.MISC));
    }

    private static <T extends Entity> EntityType<T> makeType(String name, IFactory<T> factory,
            EntityClassification classification) {
        ResourceLocation loc = new ResourceLocation(BaseDefense.MODID, name);
        EntityType<T> type = Builder.create(factory, classification).build(loc.toString());
        type.setRegistryName(loc);
        return type;
    }
}
