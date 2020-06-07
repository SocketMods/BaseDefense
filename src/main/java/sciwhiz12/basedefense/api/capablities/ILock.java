package sciwhiz12.basedefense.api.capablities;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IWorldPosCallable;

/**
 * Represents a lock, which can be unlocked and removed by {@link IKey}s.
 * 
 * @see IKey
 * @author SciWhiz12
 */
public interface ILock {
    /**
     * <p>
     * Returns {@code true} if this could be unlocked by the given {@link IKey}.
     * </p>
     * This method should <strong>never</strong> change any state.
     * <p>
     * This method being called does not indicate that an unlock action was
     * performed or successful. This method only checks if the given {@code IKey}
     * could unlock this, under the given situation (for example, a powered door
     * checking if it still has power).
     * </p>
     * 
     * @param key      The key to be checked
     * @param worldPos The world and position of the unlock action, or {@code null}
     *                     if not applicable
     * @param player   The player peforming the unlock, or {@code null} if not
     *                     applicable
     * @return {@code true} if the given {@link IKey} could unlock this, otherwise
     *         {@code false}
     */
    public boolean canUnlock(IKey key, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player);

    /**
     * <p>
     * Returns {@code true} if this can be removed by the given {@link IKey}.
     * </p>
     * This method should <strong>never</strong> change any state.
     * <p>
     * Unlike an unlock operation, in which the {@code IKey} determines if the
     * unlock operation can happen, in a removal operation the {@code ILock}
     * decides. This allows, for example, doors with unremovable locks.
     * </p>
     * 
     * @param key      The key to be checked
     * @param worldPos The world and position of the removal action, or {@code null}
     *                     if not applicable
     * @param player   The player peforming the removal, or {@code null} if not
     *                     applicable
     * @return {@code true} if the given {@link IKey} could remove this, otherwise
     *         {@code false}
     */
    public boolean canRemove(IKey key, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player);

    /**
     * <p>
     * Called when the unlock action is performed successfully.
     * </p>
     * 
     * @param key      The key
     * @param worldPos The world and position of the unlock action, or {@code null}
     *                     if not applicable
     * @param player   The player peforming the unlock, or {@code null} if not
     *                     applicable
     */
    public void onUnlock(IKey key, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player);

    /**
     * <p>
     * Called when the removal action is performed successfully.
     * </p>
     * 
     * @param key      The key
     * @param worldPos The world and position of the removal action, or {@code null}
     *                     if not applicable
     * @param player   The player peforming the removal, or {@code null} if not
     *                     applicable
     */
    public void onRemove(IKey key, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player);

}
