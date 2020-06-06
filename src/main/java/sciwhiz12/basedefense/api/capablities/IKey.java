package sciwhiz12.basedefense.api.capablities;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Represents a key, which can unlock {@link ILock}s.
 * 
 * @see ILock
 * @author SciWhiz12
 */
public interface IKey extends INBTSerializable<INBT> {
    /**
     * <p>
     * Returns {@code true} if this can unlock the given {@link ILock}.
     * </p>
     * This method should <strong>never</strong> change any state.
     * <p>
     * This method being called does not indicate that an unlock action was
     * performed or successful.
     * </p>
     * 
     * @param lock     The lock to be checked
     * @param worldPos The world and position of the unlock action, or {@code null}
     *                     if not applicable
     * @param player   The player peforming the unlock, or {@code null} if not
     *                     applicable
     * @return {@code true} if the given {@link ILock} is unlockable, otherwise
     *         {@code false}
     */
    public boolean canUnlock(ILock lock, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player);

    /**
     * <p>
     * Called when the unlock action is performed successfully.
     * </p>
     * 
     * @param lock     The lock
     * @param worldPos The world and position of the unlock action, or {@code null}
     *                     if not applicable
     * @param player   The player peforming the unlock, or {@code null} if not
     *                     applicable
     */
    public void onUnlock(ILock lock, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player);
}