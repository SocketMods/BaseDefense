package sciwhiz12.basedefense;

import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Hand;
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.ILock;

public class LockingUtil {
    public static final String NBT_UUID = "key_id";
    public static final String NBT_LOCK_KEYIDS = "unlock_ids";

    public static boolean isValidUnlock(ItemStack lock, PlayerEntity player) {
        if (isValidUnlock(lock, player.getHeldItem(Hand.MAIN_HAND))) return true;
        if (isValidUnlock(lock, player.getHeldItem(Hand.OFF_HAND))) return true;
        return false;
    }

    public static boolean isValidUnlock(ItemStack lock, ItemStack key) {
        if (lock.isEmpty() || key.isEmpty()) return false;
        if (!(lock.getItem() instanceof ILock || key.getItem() instanceof IKey)) return false;
        long key_id = getKeyID(key);
        for (long id : getUnlockIDs(lock)) { if (id == key_id) return true; }
        return false;
    }

    public static long getKeyID(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        long id = tag.getLong(NBT_UUID);
        if (id == 0) {
            id = UUID.randomUUID().getMostSignificantBits();
            stack.setTagInfo(NBT_UUID, LongNBT.valueOf(id));
        }
        return id;
    }

    public static long[] getUnlockIDs(ItemStack lock) {
        CompoundNBT tag = lock.getOrCreateTag();
        return tag.getLongArray(NBT_LOCK_KEYIDS);
    }

    public static void addUnlockID(ItemStack lock, long id) {
        long[] arr_orig = getUnlockIDs(lock);
        long[] arr = new long[arr_orig.length + 1];
        if (arr_orig.length != 0) System.arraycopy(arr_orig, 0, arr, 0, arr_orig.length);
        arr[arr.length - 1] = id;
        lock.setTagInfo(NBT_LOCK_KEYIDS, new LongArrayNBT(arr));
    }
}
