package tk.sciwhiz12.basedefense.api.capablities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IWorldPosCallable;

import javax.annotation.Nullable;

/**
 * Represents a lock, which can be unlocked and removed by {@link IKey}s.
 *
 * @author SciWhiz12
 * @see IKey
 */
public interface ILock {
    /**
     * <p>Returns {@code true} if this could be unlocked by the given {@link IKey}.</p>
     *
     * <p>This method should <strong>never</strong> change any state.</p>
     *
     * <p>This method being called does not indicate that an unlock action was
     * performed or successful. This method only checks if the given {@code IKey}
     * could unlock this, under the given situation (for example, a powered door
     * checking if it still has power).</p>
     *
     * @param key      The key to be checked
     * @param worldPos The world and position of the unlock action, if available
     * @param player   The player performing the unlock, or {@code null} if not
     *                 applicable
     * @return {@code true} if the given {@link IKey} could unlock this, otherwise
     * {@code false}
     */
    boolean canUnlock(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player);

    /**
     * <p>Returns {@code true} if this can be removed by the given {@link IKey}.</p>
     *
     * <p>This method should <strong>never</strong> change any state.</p>
     *
     * <p>Unlike an unlock operation, in which the {@code IKey} determines if the
     * unlock operation can happen, in a removal operation the {@code ILock}
     * decides. This allows, for example, doors with irremovable locks.</p>
     *
     * @param key      The key to be checked
     * @param worldPos The world and position of the removal action, if available
     * @param player   The player performing the removal, or {@code null} if not
     *                 applicable
     * @return {@code true} if the given {@link IKey} could remove this, otherwise
     * {@code false}
     */
    boolean canRemove(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player);

    /**
     * Called when the unlock action is performed successfully.
     *
     * @param key      The key
     * @param worldPos The world and position of the unlock action, if available
     * @param player   The player performing the unlock, or {@code null} if not
     *                 applicable
     */
    void onUnlock(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player);

    /**
     * Called when the removal action is performed successfully.
     *
     * @param key      The key
     * @param worldPos The world and position of the removal action, if available
     * @param player   The player performing the removal, or {@code null} if not
     *                 applicable
     */
    void onRemove(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player);

}
