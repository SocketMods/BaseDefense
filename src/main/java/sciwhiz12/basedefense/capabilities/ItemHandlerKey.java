package sciwhiz12.basedefense.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import sciwhiz12.basedefense.Util;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;
import sciwhiz12.basedefense.init.ModCapabilities;

public class ItemHandlerKey implements IKey {
    private LazyOptional<IItemHandler> itemHander;

    public ItemHandlerKey(LazyOptional<IItemHandler> hander) {
        this.itemHander = hander;
    }

    @Override
    public boolean canUnlock(ILock lock, IWorldPosCallable worldPos, PlayerEntity player) {
        return Util.applyOrDefault(itemHander, false, (item) -> {
            for (int i = 0; i < item.getSlots(); i++) {
                ItemStack stack = item.getStackInSlot(i);
                LazyOptional<IKey> keyCap = stack.getCapability(ModCapabilities.KEY);
                if (keyCap.isPresent()) {
                    if (lock.canUnlock(keyCap.orElseThrow(NullPointerException::new), worldPos, player)) { return true; }
                }
            }
            return false;
        });
    }

    @Override
    public void onUnlock(ILock lock, IWorldPosCallable worldPos, PlayerEntity player) {
        itemHander.ifPresent((item) -> {
            for (int i = 0; i < item.getSlots(); i++) {
                ItemStack stack = item.getStackInSlot(i);
                LazyOptional<IKey> keyCap = stack.getCapability(ModCapabilities.KEY);
                if (keyCap.isPresent()) {
                    IKey key = keyCap.orElseThrow(NullPointerException::new);
                    if (lock.canUnlock(key, worldPos, player)) { key.onUnlock(lock, worldPos, player); }
                }
            }
        });
    }

    @Override
    public INBT serializeNBT() {
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(INBT nbt) {}
}
