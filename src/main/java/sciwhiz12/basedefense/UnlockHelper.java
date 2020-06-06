package sciwhiz12.basedefense;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;
import sciwhiz12.basedefense.init.ModCapabilities;

public class UnlockHelper {
    public static void consumeIfPresent(ICapabilityProvider keyProvider, ICapabilityProvider lockProvider,
            BiConsumer<IKey, ILock> consumer) {
        final LazyOptional<ILock> lockCap = lockProvider.getCapability(ModCapabilities.LOCK);
        final LazyOptional<IKey> keyCap = keyProvider.getCapability(ModCapabilities.KEY);
        lockCap.ifPresent((lock) -> { keyCap.ifPresent((key) -> consumer.accept(key, lock)); });
    }

    public static UnlockResult checkUnlock(ICapabilityProvider keyProv, ICapabilityProvider lockProv,
            IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        Preconditions.checkNotNull(worldPos, "worldPos should not be null");
        if (lockProv == null) { return UnlockResult.LOCK_IS_NULL; }
        if (keyProv == null) { return UnlockResult.KEY_IS_NULL; }
        if (!lockProv.getCapability(ModCapabilities.LOCK).isPresent()) { return UnlockResult.LOCK_NO_CAP; }
        if (!keyProv.getCapability(ModCapabilities.KEY).isPresent()) { return UnlockResult.KEY_NO_CAP; }
        final AtomicReference<UnlockResult> result = new AtomicReference<>(UnlockResult.FAIL_GENERAL);
        consumeIfPresent(keyProv, lockProv, (key, lock) -> {
            if (!key.canUnlock(lock, worldPos, player)) {
                result.set(UnlockResult.FAIL_KEY);
            } else if (!lock.canUnlock(key, worldPos, player)) {
                result.set(UnlockResult.FAIL_LOCK);
            } else {
                result.set(UnlockResult.SUCCESS);
            }
        });
        return result.get();
    }

    public static boolean checkRemove(ICapabilityProvider keyProv, ICapabilityProvider lockProv, IWorldPosCallable worldPos,
            @Nullable PlayerEntity player) {
        Preconditions.checkNotNull(worldPos, "worldPos should not be null");
        if (lockProv == null) { return false; }
        if (keyProv == null) { return false; }
        if (!lockProv.getCapability(ModCapabilities.LOCK).isPresent()) { return false; }
        if (!keyProv.getCapability(ModCapabilities.KEY).isPresent()) { return false; }
        final AtomicBoolean result = new AtomicBoolean(false);
        consumeIfPresent(keyProv, lockProv, (key, lock) -> {
            if (lock.canRemove(key, worldPos, player)) {
                result.set(true);
                return;
            }
        });
        return result.get();
    }

    public static enum UnlockResult {
        LOCK_IS_NULL, KEY_IS_NULL, LOCK_NO_CAP, KEY_NO_CAP, FAIL_KEY, FAIL_LOCK, FAIL_GENERAL, SUCCESS;

        public boolean isSuccess() {
            return this == SUCCESS;
        }

        public boolean isFailure() {
            return !isSuccess();
        }
    }
}
