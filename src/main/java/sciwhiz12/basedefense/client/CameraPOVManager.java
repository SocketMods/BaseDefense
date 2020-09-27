package sciwhiz12.basedefense.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import static sciwhiz12.basedefense.BaseDefense.CLIENT;
import static sciwhiz12.basedefense.BaseDefense.LOG;
import static sciwhiz12.basedefense.Reference.MODID;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.FORGE, modid = MODID)
public class CameraPOVManager {
    public static CameraPOVManager INSTANCE = null;

    public static void register() {
        LOG.debug(CLIENT, "Creating and registering instance of camera POV manager");
        INSTANCE = new CameraPOVManager(Minecraft.getInstance());
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    private final Minecraft mc;
    private Entity prevViewEntity;
    private boolean prevHideGUI;
    private PointOfView prevThirdPerson;
    private volatile int viewEntityID = -1;
    private volatile boolean renderingCamera = false;

    CameraPOVManager(Minecraft inst) {
        this.mc = inst;
    }

    private Entity validateEntityID() {
        if (mc.world != null) {
            Entity entity = mc.world.getEntityByID(viewEntityID);
            if (entity != null && entity != mc.player) { return entity; }
        }
        viewEntityID = -1;
        return null;
    }

    public void setViewEntityID(final int entityID) {
        viewEntityID = entityID;
        validateEntityID();
    }

    public int getViewEntityID() {
        return viewEntityID;
    }

    public boolean isRenderingCamera() {
        return renderingCamera;
    }

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event) {
        if (event.phase == Phase.START && !renderingCamera) {
            Entity entity = validateEntityID();
            if (entity == null) { return; }
            prevViewEntity = mc.getRenderViewEntity();
            mc.setRenderViewEntity(entity);
            prevHideGUI = mc.gameSettings.hideGUI;
            prevThirdPerson = mc.gameSettings.getPointOfView();
            mc.gameSettings.hideGUI = true;
            mc.gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
            renderingCamera = true;
        } else if (event.phase == Phase.END && renderingCamera) {
            mc.setRenderViewEntity(prevViewEntity);
            prevViewEntity = null;
            mc.gameSettings.hideGUI = prevHideGUI;
            mc.gameSettings.setPointOfView(prevThirdPerson);
            renderingCamera = false;
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (isRenderingCamera()) {
            ClientPlayerEntity player = mc.player;
            if (mc.player == null) { return; }
            EntityRendererManager manager = mc.getRenderManager();
            float partialTicks = event.getPartialTicks();
            Vector3d projView = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
            double x = MathHelper.lerp(partialTicks, player.lastTickPosX, player.getPosX());
            double y = MathHelper.lerp(partialTicks, player.lastTickPosY, player.getPosY());
            double z = MathHelper.lerp(partialTicks, player.lastTickPosZ, player.getPosZ());
            float yaw = MathHelper.lerp(partialTicks, mc.player.prevRotationYaw, mc.player.rotationYaw);
            MatrixStack stack = event.getMatrixStack();
            stack.push();
            final IRenderTypeBuffer.Impl buffers = mc.getRenderTypeBuffers().getBufferSource();
            manager.renderEntityStatic(player, x - projView.x, y - projView.y, z - projView.z, yaw, partialTicks, stack,
                    buffers, manager.getPackedLight(player, partialTicks)
            );
            buffers.finish();
            stack.pop();
        }
    }

    @SubscribeEvent
    public void onRenderNameplate(RenderNameplateEvent event) {
        if (isRenderingCamera()) { event.setResult(Result.DENY); }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (isRenderingCamera()) { if (event.getType() == ElementType.ALL) { event.setCanceled(true); } }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (isRenderingCamera()) { event.setCanceled(true); }
    }
}
