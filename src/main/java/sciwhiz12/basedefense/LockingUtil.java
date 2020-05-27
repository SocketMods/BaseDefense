package sciwhiz12.basedefense;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.LongNBT;

public class LockingUtil {
    private static final Random KEYID_RNG = new Random();
    public static final String NBT_UUID = "key_id";
    public static final String NBT_LOCK_KEYIDS = "unlock_ids";

    public static boolean hasUnlockID(ItemStack lock, ItemStack key) {
        if (lock.isEmpty() || key.isEmpty()) return false;
        long key_id = getKeyID(key);
        for (long id : getUnlockIDs(lock)) { if (id == key_id) return true; }
        return false;
    }

    public static long getKeyID(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        long id = tag.getLong(NBT_UUID);
        if (id == 0) {
            id = KEYID_RNG.nextLong();
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

    public static void copyUnlockIDs(ItemStack from, ItemStack to) {
        to.setTagInfo(NBT_LOCK_KEYIDS, from.getOrCreateTag().get(NBT_LOCK_KEYIDS));
    }
}
