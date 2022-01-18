package tk.sciwhiz12.basedefense.api.capablities;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a key, which can unlock {@link ILock}s.
 *
 * @author SciWhiz12
 * @see ILock
 */
public interface IKey {
    /**
     * <p>Returns {@code true} if this can unlock the given {@link ILock}.</p>
     *
     * <p>This method should <strong>never</strong> change any state.</p>
     *
     * <p>This method being called does not indicate that an unlock action was
     * performed or successful.</p>
     *
     * @param lock     The lock to be checked
     * @param worldPos The world and position of the unlock action, if available
     * @param player   The player performing the unlock, or {@code null} if not
     *                 applicable
     * @return {@code true} if the given {@link ILock} is unlockable, otherwise
     * {@code false}
     */
    boolean canUnlock(ILock lock, ContainerLevelAccess worldPos, @Nullable Player player);

    /**
     * Called when the unlock action is performed successfully.
     *
     * @param lock     The lock
     * @param worldPos The world and position of the unlock action, if available
     * @param player   The player performing the unlock, or {@code null} if not
     *                 applicable
     */
    void onUnlock(ILock lock, ContainerLevelAccess worldPos, @Nullable Player player);
}
