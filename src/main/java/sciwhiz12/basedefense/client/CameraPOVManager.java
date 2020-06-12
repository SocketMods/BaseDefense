package sciwhiz12.basedefense.client;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import sciwhiz12.basedefense.BaseDefense;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.FORGE, modid = BaseDefense.MODID)
public class CameraPOVManager {
    public static volatile int cameraEntityId = -1;
    public static volatile boolean renderingCamera = false;

    private static Entity prevRenderEntity;
    private static boolean prevIgnoreFrustumCheck = false;
    private static int currentCameraEntityId;

    public static void changeEntityID(int newId) {
        Minecraft mc = Minecraft.getInstance();
        int newEntityId = newId;
        if (cameraEntityId == newId) { newEntityId = -1; }
        Entity newEntity = mc.world.getEntityByID(newEntityId);
        if (newEntityId >= 0 && newEntity != null) {
            cameraEntityId = newEntityId;
        } else {
            cameraEntityId = -1;
        }
        System.out.println(cameraEntityId);
    }

    public static final Field THIRD_PERSON = ObfuscationReflectionHelper.findField(ActiveRenderInfo.class, "field_216799_k");

    @SubscribeEvent
    static void onCameraSetup(CameraSetup event) {
        if (renderingCamera) {
            // try {
            // THIRD_PERSON.setBoolean(event.getInfo(), true);
            // }
            // catch (IllegalArgumentException | IllegalAccessException e) {
            // throw new RuntimeException("CameraSetup for player camera", e);
            // }
        }
    }

    @SubscribeEvent
    static void onRenderTick(RenderTickEvent event) {
        if (event.phase == Phase.START) {
            if (cameraEntityId >= 0 && !renderingCamera) {
                currentCameraEntityId = cameraEntityId;
                Minecraft mc = Minecraft.getInstance();
                if (mc == null || mc.world == null) return;
                Entity newEntity = mc.world.getEntityByID(currentCameraEntityId);
                if (newEntity != null) {
                    prevRenderEntity = mc.renderViewEntity;
                    if (prevRenderEntity != null) { prevIgnoreFrustumCheck = prevRenderEntity.ignoreFrustumCheck; }
                    prevRenderEntity.ignoreFrustumCheck = true;
                    mc.setRenderViewEntity(newEntity);
                    renderingCamera = true;
                } else {
                    cameraEntityId = -1;
                }
            }
        } else if (event.phase == Phase.END) {
            if (renderingCamera) {
                Minecraft mc = Minecraft.getInstance();
                prevRenderEntity.ignoreFrustumCheck = prevIgnoreFrustumCheck;
                mc.setRenderViewEntity(prevRenderEntity);
                prevRenderEntity = null;
                renderingCamera = false;
            }
        }
    }
}
