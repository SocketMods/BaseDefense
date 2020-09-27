package sciwhiz12.basedefense.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import sciwhiz12.basedefense.Reference.TileEntities;

import static sciwhiz12.basedefense.block.PTZCameraBlock.FACING;

public class PTZCameraTile extends TileEntity implements ITickableTileEntity {
    private int firstTick = 0;

    private double pitch = 0F;
    private double yaw = 0F;

    private float maxLeftYaw = -10F;
    private float maxRightYaw = 10F;

    private boolean autoEnabled = true;
    private double autoYawDrift = 3.5F;
    private double autoYawLeftMax = -60F;
    private double autoYawRightMax = 60F;

    public PTZCameraTile() {
        super(TileEntities.PTZ_CAMERA);
    }

    public double getYaw() {
        return yaw;
    }

    public double getRenderYaw() {
        return yaw / 90F;
    }

    public double getPitch() {
        return pitch;
    }

    @Override
    public void tick() {
        if (firstTick++ <= 0) { calculateMaxYaw(); }
        if (world != null && world.getGameTime() % 200 == 0) { calculateMaxYaw(); }
        if (autoEnabled) {
            yaw += autoYawDrift;
            double autoLeft = Math.max(autoYawLeftMax, maxLeftYaw);
            double autoRight = Math.min(autoYawRightMax, maxRightYaw);
            if (yaw <= autoLeft || yaw >= autoRight) {
                autoYawDrift = -autoYawDrift;
                yaw += autoYawDrift;
            }
            yaw = MathHelper.clamp(yaw, maxLeftYaw, maxRightYaw);
        }
    }

    public void calculateMaxYaw() {
        if (world == null) { return; }
        final BlockState state = getBlockState();
        final Direction dir = state.get(FACING);
        final Direction leftDir = dir.rotateYCCW();
        final Direction rightDir = dir.rotateY();
        final BlockPos leftPos = pos.offset(leftDir);
        final BlockPos rightPos = pos.offset(rightDir);
        final boolean leftSolid = world.getBlockState(leftPos).isSolidSide(world, leftPos, leftDir.getOpposite());
        final boolean rightSolid = world.getBlockState(rightPos).isSolidSide(world, rightPos, rightDir.getOpposite());
        maxLeftYaw = leftSolid ? -10F : -120F;
        maxRightYaw = rightSolid ? 10F : 120F;
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putDouble("Yaw", yaw);
        compound.putDouble("Pitch", pitch);
        CompoundNBT autoTag = new CompoundNBT();
        autoTag.putBoolean("Enabled", autoEnabled);

        CompoundNBT yaw = new CompoundNBT();
        yaw.putDouble("Drift", autoYawDrift);

        ListNBT boundsList = new ListNBT();
        boundsList.add(0, DoubleNBT.valueOf(autoYawLeftMax));
        boundsList.add(1, DoubleNBT.valueOf(autoYawRightMax));
        yaw.put("Bounds", boundsList);

        autoTag.put("Yaw", yaw);

        compound.put("Automation", autoTag);
        return compound;
    }

    public void read(CompoundNBT compound) {
        yaw = compound.getDouble("Yaw");
        pitch = compound.getDouble("Pitch");
        if (compound.contains("Automation", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT autoTag = compound.getCompound("Automation");
            autoEnabled = autoTag.getBoolean("Enabled");
            if (autoTag.contains("Yaw", Constants.NBT.TAG_COMPOUND)) {
                CompoundNBT yawTag = autoTag.getCompound("Yaw");
                if (yawTag.contains("Drift", Constants.NBT.TAG_ANY_NUMERIC)) { autoYawDrift = yawTag.getDouble("Drift"); }
                if (yawTag.contains("Bounds", Constants.NBT.TAG_LIST)) {
                    ListNBT bounds = yawTag.getList("Bounds", Constants.NBT.TAG_DOUBLE);
                    autoYawLeftMax = bounds.getDouble(0);
                    autoYawRightMax = bounds.getDouble(1);
                }
            }
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.read(compound);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
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
