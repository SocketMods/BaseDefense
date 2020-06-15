package sciwhiz12.basedefense.util;

import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;
import sciwhiz12.basedefense.init.ModCapabilities;

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
        final LazyOptional<ILock> lockCap = lockProvider.getCapability(ModCapabilities.LOCK);
        final LazyOptional<IKey> keyCap = keyProvider.getCapability(ModCapabilities.KEY);
        Util.consumeIfPresent(keyCap, lockCap, consumer);
    }

    public static boolean checkUnlock(ICapabilityProvider keyProv, ICapabilityProvider lockProv, @Nullable World world,
            @Nullable BlockPos pos, @Nullable PlayerEntity player) {
        return checkUnlock(keyProv, lockProv, Util.getOrDummy(world, pos), player);
    }

    public static boolean checkUnlock(ICapabilityProvider keyProv, ICapabilityProvider lockProv, IWorldPosCallable worldPos,
            @Nullable PlayerEntity player) {
        Preconditions.checkNotNull(keyProv);
        Preconditions.checkNotNull(lockProv);
        Preconditions.checkNotNull(worldPos);
        return Util.mapIfBothPresent(lockProv.getCapability(ModCapabilities.LOCK), keyProv.getCapability(
            ModCapabilities.KEY), false, (lock, key) -> key.canUnlock(lock, worldPos, player) && lock.canUnlock(key,
                worldPos, player));
    }

    public static boolean checkRemove(ICapabilityProvider keyProv, ICapabilityProvider lockProv, @Nullable World world,
            @Nullable BlockPos pos, @Nullable PlayerEntity player) {
        return checkRemove(keyProv, lockProv, Util.getOrDummy(world, pos), player);
    }

    public static boolean checkRemove(ICapabilityProvider keyProv, ICapabilityProvider lockProv, IWorldPosCallable worldPos,
            @Nullable PlayerEntity player) {
        Preconditions.checkNotNull(keyProv);
        Preconditions.checkNotNull(lockProv);
        Preconditions.checkNotNull(worldPos);
        return Util.mapIfBothPresent(lockProv.getCapability(ModCapabilities.LOCK), keyProv.getCapability(
            ModCapabilities.KEY), false, (lock, key) -> lock.canRemove(key, worldPos, player));
    }
}
