package sciwhiz12.basedefense.capabilities;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.util.INBTSerializable;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;

/**
 * An implementation of {@link ILock} that extends {@link CodeHolder}.
 * <p>
 * Used as the default implementation of the {@code ILock} capability. Can be
 * used as a base class.
 * 
 * @author SciWhiz12
 */
public class CodedLock extends CodeHolder implements ILock, INBTSerializable<LongArrayNBT> {
    @Override
    public boolean canRemove(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
        return key.canUnlock(this, worldPos, player) && this.canUnlock(key, worldPos, player);
    }

    @Override
    public boolean canUnlock(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
        return true;
    }

    @Override
    public void onRemove(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {}

    @Override
    public void onUnlock(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {}

    @Override
    public LongArrayNBT serializeNBT() {
        return new LongArrayNBT(storedCodes.toLongArray());
    }

    @Override
    public void deserializeNBT(LongArrayNBT nbt) {
        this.storedCodes = new LongArrayList(((LongArrayNBT) nbt).getAsLongArray());
    }
}
