package sciwhiz12.basedefense.net;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import sciwhiz12.basedefense.client.CameraPOVManager;

public class ChangePOVPacket {
    public final int entityId;

    public ChangePOVPacket(int entityId) {
        this.entityId = entityId;
    }

    public ChangePOVPacket(Entity entity) {
        this.entityId = entity.getEntityId();
    }

    public ChangePOVPacket() {
        this.entityId = -1;
    }

    public static void encode(ChangePOVPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.entityId);
    }

    public static ChangePOVPacket decode(PacketBuffer buf) {
        return new ChangePOVPacket(buf.readInt());
    }

    public static boolean process(ChangePOVPacket pkt, Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> { CameraPOVManager.INSTANCE.setViewEntityID(pkt.entityId); });
        return true;
    }
}
