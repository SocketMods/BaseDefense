package sciwhiz12.basedefense.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import sciwhiz12.basedefense.capabilities.CodedItemStackLock;
import sciwhiz12.basedefense.init.ModCapabilities;
import sciwhiz12.basedefense.init.ModTileEntities;

public class LockableTile extends TileEntity {
    public static final String TAG_LOCK = "Lock";

    private final LazyOptional<CodedItemStackLock> lockCap = LazyOptional.of(() -> this.lock);
    private final CodedItemStackLock lock = new CodedItemStackLock();

    public LockableTile() {
        super(ModTileEntities.LOCKABLE_TILE);
    }

    public LockableTile(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ModCapabilities.LOCK) { return lockCap.cast(); }
        return super.getCapability(cap, side);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        tag.put(TAG_LOCK, lock.getStack().write(new CompoundNBT()));
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.lock.setStack(ItemStack.read(pkt.getNbtCompound().getCompound(TAG_LOCK)));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        lock.setStack(ItemStack.read(compound.getCompound(TAG_LOCK)));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.put(TAG_LOCK, lock.getStack().write(new CompoundNBT()));
        return compound;
    }

    @Override
    public void remove() {
        super.remove();
        lockCap.invalidate();
    }

    public void setLockStack(ItemStack stack) {
        lock.setStack(stack);
        this.world.notifyBlockUpdate(pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT);
        this.markDirty();
    }

    public ItemStack getLockStack() {
        return lock.getStack();
    }
}
