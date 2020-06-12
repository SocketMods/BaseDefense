package sciwhiz12.basedefense.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sciwhiz12.basedefense.BaseDefense;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.FORGE, modid = BaseDefense.MODID)
public class CameraPOVManager {
    public static volatile int cameraEntityId = -1;
    public static volatile boolean renderingCamera = false;

    private static Entity prevRenderEntity;
    private static int currentCameraEntityId;

    public static void changeEntityID(int newId) {
        Minecraft mc = Minecraft.getInstance();
        int newEntityId = newId;
        if (newId != mc.player.getEntityId() && cameraEntityId != newEntityId) {
            Entity newEntity = mc.world.getEntityByID(newEntityId);
            if (newEntityId >= 0 && newEntity != null) {
                cameraEntityId = newEntityId;
            } else {
                cameraEntityId = -1;
            }
        } else {
            cameraEntityId = -1;
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
                    mc.setRenderViewEntity(newEntity);
                    renderingCamera = true;
                } else {
                    cameraEntityId = -1;
                }
            }
        } else if (event.phase == Phase.END) {
            if (renderingCamera) {
                Minecraft mc = Minecraft.getInstance();
                mc.setRenderViewEntity(prevRenderEntity);
                prevRenderEntity = null;
                renderingCamera = false;
            }
        }
    }

    @SubscribeEvent
    static void onRenderWorldLast(RenderWorldLastEvent event) {
        if (renderingCamera) {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null) return;
            EntityRendererManager manager = mc.getRenderManager();
            float partialTicks = event.getPartialTicks();
            Entity entity = mc.player;
            IRenderTypeBuffer buffer = mc.getRenderTypeBuffers().getBufferSource();
            Vec3d projectedView = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
            double x = MathHelper.lerp((double) partialTicks, entity.lastTickPosX, entity.getPosX());
            double y = MathHelper.lerp((double) partialTicks, entity.lastTickPosY, entity.getPosY());
            double z = MathHelper.lerp((double) partialTicks, entity.lastTickPosZ, entity.getPosZ());
            float yaw = MathHelper.lerp(partialTicks, entity.prevRotationYaw, entity.rotationYaw);
            manager.renderEntityStatic(entity, x - projectedView.getX(), y - projectedView.getY(), z - projectedView.getZ(),
                yaw, partialTicks, event.getMatrixStack(), buffer, manager.getPackedLight(entity, partialTicks));
        }
    }
}
