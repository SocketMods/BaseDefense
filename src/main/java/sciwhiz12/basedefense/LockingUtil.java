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
    public static final String NBT_UUID = "unique_id";
    public static final String NBT_KEY_COMPOUND = "ancestors";

    public static boolean isValidUnlock(ItemStack lock, PlayerEntity player) {
        if (isValidUnlock(lock, player.getHeldItem(Hand.MAIN_HAND))) return true;
        if (isValidUnlock(lock, player.getHeldItem(Hand.OFF_HAND))) return true;
        return false;
    }

    public static boolean isValidUnlock(ItemStack lock, ItemStack key) {
        if (lock == null || lock == ItemStack.EMPTY) return false;
        if (key == null || key == ItemStack.EMPTY) return false;
        if (!(lock.getItem() instanceof ILock || key.getItem() instanceof IKey)) return false;
        long key_id = getID(key);
        if (getID(lock) == key_id) return true;
        for (long id : getAncestorIDs(key)) { if (id == key_id) return true; }
        return false;
    }

    public static long getID(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        long id = tag.getLong(NBT_UUID);
        if (id == 0) {
            id = UUID.randomUUID().getMostSignificantBits();
            stack.setTagInfo(NBT_UUID, LongNBT.valueOf(id));
        }
        return id;
    }

    public static long[] getAncestorIDs(ItemStack key) {
        CompoundNBT tag = key.getOrCreateTag();
        return tag.getLongArray(NBT_KEY_COMPOUND);
    }

    public static void addAncestorID(ItemStack key, long id) {
        long[] arr_orig = getAncestorIDs(key);
        long[] arr = new long[arr_orig.length + 1];
        if (arr_orig.length != 0) System.arraycopy(arr_orig, 0, arr, 0, arr.length);
        arr[arr.length - 1] = id;
        key.setTagInfo(NBT_KEY_COMPOUND, new LongArrayNBT(arr));
    }
}
