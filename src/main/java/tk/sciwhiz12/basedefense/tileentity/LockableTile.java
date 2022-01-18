package tk.sciwhiz12.basedefense.tileentity;

import net.minecraft.block.BlockState;
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
import tk.sciwhiz12.basedefense.Reference.TileEntities;
import tk.sciwhiz12.basedefense.capabilities.CodedItemStackLock;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.*;

public class LockableTile extends TileEntity {
    public static final String TAG_LOCK_ITEM = "LockItem";

    protected LazyOptional<CodedItemStackLock> lockCap;
    protected final CodedItemStackLock lock = new CodedItemStackLock();

    public LockableTile() {
        super(TileEntities.LOCKABLE_TILE);
    }

    public LockableTile(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CONTAINS_CODE || cap == CODE_HOLDER || cap == LOCK) {
            if (lockCap == null) {
                lockCap = LazyOptional.of(() -> this.lock);
            }
            return lockCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        tag.put(TAG_LOCK_ITEM, lock.getStack().save(new CompoundNBT()));
        return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.lock.setStack(ItemStack.of(pkt.getTag().getCompound(TAG_LOCK_ITEM)));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        readData(compound);
    }

    public void readData(CompoundNBT compound) {
        lock.setStack(ItemStack.of(compound.getCompound(TAG_LOCK_ITEM)));
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound = super.save(compound);
        compound = writeData(compound);
        return compound;
    }

    public CompoundNBT writeData(CompoundNBT compound) {
        compound.put(TAG_LOCK_ITEM, lock.getStack().save(new CompoundNBT()));
        return compound;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (lockCap != null) {
            lockCap.invalidate();
            lockCap = null;
        }
    }

    public void setLockStack(ItemStack stack) {
        lock.setStack(stack);
        if (level != null) {
            this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.DEFAULT);
        }
        this.setChanged();
        if (lockCap != null) {
            lockCap.invalidate();
            lockCap = null;
        }
    }

    public ItemStack getLockStack() {
        return lock.getStack();
    }
}
