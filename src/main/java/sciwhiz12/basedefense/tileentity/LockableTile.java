package sciwhiz12.basedefense.tileentity;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import sciwhiz12.basedefense.init.ModTileEntities;

public class LockableTile extends TileEntity {
    protected ItemStack lock = ItemStack.EMPTY;

    public LockableTile() {
        super(ModTileEntities.LOCKABLE_TILE);
    }
    
    public LockableTile(TileEntityType<?> type) {
        super(type);
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        write(tag);
        return new SUpdateTileEntityPacket(this.pos, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = new CompoundNBT();
        write(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        this.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT parentTag) {
        super.write(parentTag);
        CompoundNBT itemStackNBT = new CompoundNBT();
        this.lock.write(itemStackNBT);
        parentTag.put("LockItem", itemStackNBT);
        return parentTag;
    }

    @Override
    public void read(CompoundNBT parentTag) {
        super.read(parentTag);
        CompoundNBT itemStackNBT = parentTag.getCompound("LockItem");
        this.lock = ItemStack.read(itemStackNBT);
    }

    public ItemStack getLock() {
        return lock.copy();
    }

    public boolean hasLock() {
        return !lock.isEmpty();
    }

    public void setLock(ItemStack stack) {
        this.lock = stack.copy();
        this.markDirty();
    }
}
