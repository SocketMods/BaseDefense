package sciwhiz12.basedefense.capabilities;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.util.INBTSerializable;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;
import sciwhiz12.basedefense.api.capablities.IContainsCode;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;

/**
 * An implementation of {@link IKey} and {@link ICodeHolder}.
 * <p>
 * Used as the default implementation of the {@code IKey} capability. Can be
 * used as a base class.
 * 
 * @author SciWhiz12
 */
public class CodedKey implements ICodeHolder, IKey, INBTSerializable<LongNBT> {
    protected Long storedCode = null;

    @Override
    public boolean canUnlock(ILock lock, IWorldPosCallable worldPos, PlayerEntity player) {
        if (lock instanceof IContainsCode) {
            IContainsCode codeLock = (IContainsCode) lock;
            return codeLock.containsCode(storedCode);
        }
        return false;
    }

    @Override
    public void onUnlock(ILock lock, IWorldPosCallable worldPos, PlayerEntity player) {}

    @Override
    public boolean containsCode(Long code) {
        return code != null && storedCode != null && code.longValue() == storedCode.longValue();
    }

    @Override
    public List<Long> getCodes() {
        return storedCode != null ? ImmutableList.of(storedCode) : ImmutableList.of();
    }

    /**
     * <strong>Impl. Note: Only takes the first entry in the list.</strong><br/>
     * {@inheritDoc}
     */
    @Override
    public void setCodes(List<Long> codes) {
        if (codes == null) { throw new NullPointerException(); }
        this.storedCode = codes.size() > 0 ? codes.get(0) : null;
    }

    /**
     * <strong>Impl. Note: Overwrites the currently stored code</strong><br/>
     * {@inheritDoc}
     */
    @Override
    public void addCode(Long code) {
        if (code == null) { throw new NullPointerException(); }
        this.storedCode = code;
    }

    @Override
    public void removeCode(Long code) {
        if (code == null) { throw new NullPointerException(); }
        if (this.storedCode.longValue() == code.longValue()) { this.storedCode = null; }
    }

    @Override
    public LongNBT serializeNBT() {
        return this.storedCode != null ? LongNBT.valueOf(this.storedCode) : null;
    }

    @Override
    public void deserializeNBT(LongNBT nbt) {
        this.storedCode = nbt.getLong();
    }
}
