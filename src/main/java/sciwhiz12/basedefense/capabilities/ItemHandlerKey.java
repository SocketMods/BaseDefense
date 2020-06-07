package sciwhiz12.basedefense.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
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
        return itemHander.map((item) -> {
            for (int i = 0; i < item.getSlots(); i++) {
                ItemStack stack = item.getStackInSlot(i);
                if (stack.getCapability(ModCapabilities.KEY).map((key) -> lock.canUnlock(key, worldPos, player)).orElse(
                    false)) {
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }

    @Override
    public void onUnlock(ILock lock, IWorldPosCallable worldPos, PlayerEntity player) {
        itemHander.ifPresent((item) -> {
            for (int i = 0; i < item.getSlots(); i++) {
                ItemStack stack = item.getStackInSlot(i);
                if (stack.getCapability(ModCapabilities.KEY).filter((key) -> lock.canUnlock(key, worldPos, player)).map((
                        key) -> {
                    key.onUnlock(lock, worldPos, player);
                    return true;
                }).orElse(false)) { return; }
            }
        });
    }
}
