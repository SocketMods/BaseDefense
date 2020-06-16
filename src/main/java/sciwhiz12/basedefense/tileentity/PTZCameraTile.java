package sciwhiz12.basedefense.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import sciwhiz12.basedefense.block.PTZCameraBlock;
import sciwhiz12.basedefense.init.ModTileEntities;

public class PTZCameraTile extends TileEntity implements ITickableTileEntity {
    private float pitch = 0F;
    private float yaw = 0F;
    private float yawDrift = 0.05F;

    public PTZCameraTile() {
        super(ModTileEntities.PTZ_CAMERA);
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public void tick() {
        if (getBlockState().get(PTZCameraBlock.ENABLED)) {
            yaw += yawDrift;
            if (Math.abs(yaw) >= 1.5 && Math.signum(yaw) == Math.signum(yawDrift)) {
                yawDrift = -yawDrift;
                yaw += yawDrift;
            }
        }
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putFloat("Yaw", yaw);
        compound.putFloat("YawDrift", yawDrift);
        compound.putFloat("Pitch", pitch);
        return compound;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        yaw = compound.getFloat("Yaw");
        pitch = compound.getFloat("Pitch");
        if (compound.contains("YawDrift", Constants.NBT.TAG_ANY_NUMERIC)) { yawDrift = compound.getFloat("YawDrift"); }
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = this.write(new CompoundNBT());
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }
}
