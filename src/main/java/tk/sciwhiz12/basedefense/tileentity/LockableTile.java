package tk.sciwhiz12.basedefense.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tk.sciwhiz12.basedefense.Reference.TileEntities;
import tk.sciwhiz12.basedefense.capabilities.CodedItemStackLock;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.CONTAINS_CODE;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;

public class LockableTile extends BlockEntity {
    public static final String TAG_LOCK_ITEM = "LockItem";

    protected LazyOptional<CodedItemStackLock> lockCap;
    protected final CodedItemStackLock lock = new CodedItemStackLock();

    public LockableTile(BlockPos pos, BlockState state) {
        super(TileEntities.LOCKABLE_TILE, pos, state);
    }

    public LockableTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag tag = new CompoundTag();
        tag.put(TAG_LOCK_ITEM, lock.getStack().save(new CompoundTag()));
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.lock.setStack(ItemStack.of(pkt.getTag().getCompound(TAG_LOCK_ITEM)));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        readData(compound);
    }

    public void readData(CompoundTag compound) {
        lock.setStack(ItemStack.of(compound.getCompound(TAG_LOCK_ITEM)));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        writeData(compound);
    }

    public void writeData(CompoundTag compound) {
        compound.put(TAG_LOCK_ITEM, lock.getStack().save(new CompoundTag()));
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
            this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
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
