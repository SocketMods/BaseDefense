package tk.sciwhiz12.basedefense.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.api.capablities.IKey;
import tk.sciwhiz12.basedefense.api.capablities.ILock;

import java.util.function.BiConsumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.KEY;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static tk.sciwhiz12.basedefense.util.Util.mapIfBothPresent;

/**
 * Helper methods for unlocking and removing {@link ILock}s using {@link IKey}s.
 *
 * @author SciWhiz12
 */
public final class UnlockHelper {
    // Prevent instantiation
    private UnlockHelper() {
    }

    public static void consumeIfPresent(@Nullable ICapabilityProvider keyProvider,
                                        @Nullable ICapabilityProvider lockProvider, BiConsumer<IKey, ILock> consumer) {
        if (keyProvider == null) {
            return;
        }
        if (lockProvider == null) {
            return;
        }
        final LazyOptional<ILock> lockCap = lockProvider.getCapability(LOCK);
        final LazyOptional<IKey> keyCap = keyProvider.getCapability(KEY);
        Util.consumeIfPresent(keyCap, lockCap, consumer);
    }

    public static boolean checkUnlock(final ICapabilityProvider keyProv, final ICapabilityProvider lockProv,
                                      final @Nullable Level world, final @Nullable BlockPos pos, final @Nullable Player player,
                                      final boolean onUnlock) {
        return checkUnlock(keyProv, lockProv, Util.getOrDummy(world, pos), player, onUnlock);
    }

    public static boolean checkUnlock(final ICapabilityProvider keyProv, final ICapabilityProvider lockProv,
                                      final ContainerLevelAccess worldPos, final @Nullable Player player, final boolean onUnlock) {
        checkNotNull(keyProv);
        checkNotNull(lockProv);
        checkNotNull(worldPos);
        return mapIfBothPresent(lockProv.getCapability(LOCK), keyProv.getCapability(KEY), false, (lock, key) -> {
            boolean success;
            if (success = key.canUnlock(lock, worldPos, player) && lock.canUnlock(key, worldPos, player)) {
                if (onUnlock) {
                    key.onUnlock(lock, worldPos, player);
                    lock.onUnlock(key, worldPos, player);
                }
            }
            return success;
        });
    }

    public static boolean checkRemove(final ICapabilityProvider keyProv, final ICapabilityProvider lockProv,
                                      final @Nullable Level world, final @Nullable BlockPos pos, final @Nullable Player player,
                                      final boolean onUnlock) {
        return checkRemove(keyProv, lockProv, Util.getOrDummy(world, pos), player, onUnlock);
    }

    public static boolean checkRemove(final ICapabilityProvider keyProv, final ICapabilityProvider lockProv,
                                      final ContainerLevelAccess worldPos, final @Nullable Player player, final boolean onUnlock) {
        checkNotNull(keyProv);
        checkNotNull(lockProv);
        checkNotNull(worldPos);
        return mapIfBothPresent(lockProv.getCapability(LOCK), keyProv.getCapability(KEY), false, (lock, key) -> {
            boolean success;
            if (success = lock.canRemove(key, worldPos, player)) {
                if (onUnlock) {
                    lock.onRemove(key, worldPos, player);
                }
            }
            return success;
        });
    }
}
