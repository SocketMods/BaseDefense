package tk.sciwhiz12.basedefense.capabilities;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraftforge.common.util.INBTSerializable;
import tk.sciwhiz12.basedefense.api.capablities.IKey;
import tk.sciwhiz12.basedefense.api.capablities.ILock;

import javax.annotation.Nullable;

/**
 * <p>An implementation of {@link ILock} that extends {@link CodeHolder}.</p>
 *
 * <p>Used as the default implementation of the {@code ILock} capability. Can be
 * used as a base class.</p>
 *
 * @author SciWhiz12
 */
public class CodedLock extends CodeHolder implements ILock, INBTSerializable<LongArrayTag> {
    @Override
    public boolean canRemove(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {
        return key.canUnlock(this, worldPos, player) && this.canUnlock(key, worldPos, player);
    }

    @Override
    public boolean canUnlock(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {
        return true;
    }

    @Override
    public void onRemove(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {}

    @Override
    public void onUnlock(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {}

    @Override
    public LongArrayTag serializeNBT() {
        return new LongArrayTag(storedCodes.toLongArray());
    }

    @Override
    public void deserializeNBT(LongArrayTag nbt) {
        this.storedCodes = new LongArrayList(nbt.getAsLongArray());
    }
}
