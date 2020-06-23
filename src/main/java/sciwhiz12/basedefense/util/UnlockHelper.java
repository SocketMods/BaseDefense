package sciwhiz12.basedefense.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static sciwhiz12.basedefense.init.ModCapabilities.KEY;
import static sciwhiz12.basedefense.init.ModCapabilities.LOCK;
import static sciwhiz12.basedefense.util.Util.mapIfBothPresent;

import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;

/**
 * Helper methods for unlocking and removing {@link ILock}s using {@link IKey}s.
 * 
 * @author SciWhiz12
 */
public final class UnlockHelper {
    // Prevent instantiation
    private UnlockHelper() {}

    public static void consumeIfPresent(@Nullable ICapabilityProvider keyProvider,
            @Nullable ICapabilityProvider lockProvider, BiConsumer<IKey, ILock> consumer) {
        if (keyProvider == null) { return; }
        if (lockProvider == null) { return; }
        final LazyOptional<ILock> lockCap = lockProvider.getCapability(LOCK);
        final LazyOptional<IKey> keyCap = keyProvider.getCapability(KEY);
        Util.consumeIfPresent(keyCap, lockCap, consumer);
    }

    public static boolean checkUnlock(ICapabilityProvider keyProv, ICapabilityProvider lockProv, @Nullable World world,
            @Nullable BlockPos pos, @Nullable PlayerEntity player) {
        return checkUnlock(keyProv, lockProv, Util.getOrDummy(world, pos), player);
    }

    public static boolean checkUnlock(ICapabilityProvider keyProv, ICapabilityProvider lockProv, IWorldPosCallable worldPos,
            @Nullable PlayerEntity player) {
        checkNotNull(keyProv);
        checkNotNull(lockProv);
        checkNotNull(worldPos);
        return mapIfBothPresent(
            lockProv.getCapability(LOCK), keyProv.getCapability(KEY), false, (lock, key) -> key.canUnlock(
                lock, worldPos, player
            ) && lock.canUnlock(key, worldPos, player)
        );
    }

    public static boolean checkRemove(ICapabilityProvider keyProv, ICapabilityProvider lockProv, @Nullable World world,
            @Nullable BlockPos pos, @Nullable PlayerEntity player) {
        return checkRemove(keyProv, lockProv, Util.getOrDummy(world, pos), player);
    }

    public static boolean checkRemove(ICapabilityProvider keyProv, ICapabilityProvider lockProv, IWorldPosCallable worldPos,
            @Nullable PlayerEntity player) {
        checkNotNull(keyProv);
        checkNotNull(lockProv);
        checkNotNull(worldPos);
        return mapIfBothPresent(
            lockProv.getCapability(LOCK), keyProv.getCapability(KEY), false, (lock, key) -> lock.canRemove(
                key, worldPos, player
            )
        );
    }
}
