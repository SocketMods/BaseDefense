package sciwhiz12.basedefense.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sciwhiz12.basedefense.BaseDefense;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.FORGE, modid = BaseDefense.MODID)
public class CameraPOVManager {
    public static CameraPOVManager INSTANCE = null;

    public static void register() {
        INSTANCE = new CameraPOVManager(Minecraft.getInstance());
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public final Minecraft mc;
    public volatile boolean renderingCamera = false;

    public CameraPOVManager(Minecraft inst) {
        this.mc = inst;
    }

    public void changeRenderViewEntity(final int newEntityId) {
        if (mc.world == null) { return; }
        Entity newEntity = mc.world.getEntityByID(newEntityId);
        mc.setRenderViewEntity(newEntity);
    }

    public boolean isCameraRendering() {
        // return !(mc.renderViewEntity instanceof PlayerEntity);
        return true;
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (isCameraRendering()) {
            EntityRendererManager manager = mc.getRenderManager();
            ActiveRenderInfo renderInfo = mc.gameRenderer.getActiveRenderInfo();
            float partialTicks = event.getPartialTicks();
            ClientPlayerEntity player = mc.player;
            IRenderTypeBuffer.Impl buffer = mc.getRenderTypeBuffers().getBufferSource();
            Vec3d projView = renderInfo.getProjectedView();
            double x = MathHelper.lerp((double) partialTicks, player.lastTickPosX, player.getPosX());
            double y = MathHelper.lerp((double) partialTicks, player.lastTickPosY, player.getPosY());
            double z = MathHelper.lerp((double) partialTicks, player.lastTickPosZ, player.getPosZ());
            float yaw = MathHelper.lerp(partialTicks, mc.player.prevRotationYaw, mc.player.rotationYaw);
            int packedLight = manager.getPackedLight(player, partialTicks);
            MatrixStack stack = event.getMatrixStack();

            manager.renderEntityStatic(player, x - projView.x, y - projView.y, z - projView.z, yaw, partialTicks, stack,
                buffer, packedLight);
        }
    }

    @SubscribeEvent
    public void onRenderNameplate(RenderNameplateEvent event) {
        if (isCameraRendering()) { event.setResult(Result.DENY); }
    }
}
