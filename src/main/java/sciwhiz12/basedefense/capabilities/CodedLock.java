package sciwhiz12.basedefense.capabilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.primitives.Longs;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.util.IWorldPosCallable;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;

/**
 * A blank implementation of {@link ILock}.
 * <p>
 * Used as the default implementation of the {@code ILock} capability. Can be
 * used as a base class.
 * 
 * @author SciWhiz12
 */
public class CodedLock implements ILock {
    protected List<Long> codes = new ArrayList<>();

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

    public List<Long> getCodes() {
        return Collections.unmodifiableList(this.codes);
    }

    public boolean containsCode(long code) {
        return this.codes.contains(code);
    }

    public void addCode(long code) {
        this.codes.add(code);
    }

    public void removeCode(long code) {
        this.codes.remove((Long) code);
    }

    @Override
    public INBT serializeNBT() {
        return new LongArrayNBT(ArrayUtils.toPrimitive(codes.toArray(new Long[0]), -1));
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        if (nbt instanceof LongArrayNBT) {
            this.codes.clear();
            this.codes.addAll(Longs.asList(((LongArrayNBT) nbt).getAsLongArray()));
        }
    }
}
