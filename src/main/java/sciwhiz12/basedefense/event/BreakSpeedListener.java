package sciwhiz12.basedefense.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sciwhiz12.basedefense.Config;
import sciwhiz12.basedefense.world.ProtectedChunksSavedData;

import static sciwhiz12.basedefense.Reference.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Bus.FORGE)
public class BreakSpeedListener {
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        if (!player.world.isRemote && Config.Server.enableBreakSpeedModifier.get()) {
            ServerWorld world = (ServerWorld) player.world;
            if (ProtectedChunksSavedData.getFromWorld(world)
                    .isChunkOwned(world.getChunk(event.getPos()).getPos().asLong())) {
                float speed = event.getNewSpeed();
                speed *= Config.Server.breakSpeedModifier.get();
                event.setNewSpeed(speed);
            }
        }
    }
}
