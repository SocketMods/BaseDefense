package sciwhiz12.basedefense.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.IWorldPosCallable;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;

/**
 * A blank implementation of {@link IKey}.
 * <p>
 * Used as the default implementation of the {@code IKey} capability. Can be
 * used as a base class.
 * 
 * @author SciWhiz12
 */
public class CodedKey implements IKey {
    protected long code = -1L;

    @Override
    public boolean canUnlock(ILock lock, IWorldPosCallable worldPos, PlayerEntity player) {
        System.out.println("checking");
        if (lock instanceof CodedLock) {
            System.out.println("CODED LOCK");
            CodedLock codeLock = (CodedLock) lock;
            return codeLock.containsCode(this.code);
        }
        return false;
    }

    @Override
    public void onUnlock(ILock lock, IWorldPosCallable worldPos, PlayerEntity player) {}

    public void setCode(long code) {
        this.code = code;
    }

    public long getCode() {
        return this.code;
    }

    @Override
    public INBT serializeNBT() {
        return LongNBT.valueOf(this.code);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        if (nbt instanceof LongNBT) { this.code = ((LongNBT) nbt).getLong(); }
    }
}
