package sciwhiz12.basedefense.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;
import sciwhiz12.basedefense.Reference.Entities;

public class PTZCameraEntity extends Entity {
    public static final Rotations DEFAULT_ROTATION = new Rotations(0F, 0F, 0F);
    private static final DataParameter<Rotations> ROTATION_DATA = EntityDataManager.createKey(
        PTZCameraEntity.class, DataSerializers.ROTATIONS
    );
    private Rotations ROTATION = DEFAULT_ROTATION;

    public PTZCameraEntity(EntityType<?> type, World worldIn) {
        super(type, worldIn);
    }

    public PTZCameraEntity(World world, double posX, double posY, double posZ) {
        this(Entities.PTZ_CAMERA, world);
        this.setPosition(posX, posY, posZ);
    }

    protected void registerData() {
        this.dataManager.register(ROTATION_DATA, DEFAULT_ROTATION);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {}

    @Override
    protected void writeAdditional(CompoundNBT compound) {}

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }
}
