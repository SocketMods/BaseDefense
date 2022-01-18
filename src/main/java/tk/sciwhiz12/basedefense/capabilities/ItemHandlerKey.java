package tk.sciwhiz12.basedefense.capabilities;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraftforge.items.IItemHandler;
import tk.sciwhiz12.basedefense.api.capablities.IKey;
import tk.sciwhiz12.basedefense.api.capablities.ILock;

import javax.annotation.Nullable;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.KEY;

/**
 * Implementation of {@link IKey} which defers to checking a given
 * {@link IItemHandler} for unlocking.
 *
 * @author SciWhiz12
 */
public class ItemHandlerKey implements IKey {
    private final IItemHandler itemHandler;

    public ItemHandlerKey(IItemHandler handler) {
        this.itemHandler = handler;
    }

    @Override
    public boolean canUnlock(ILock lock, ContainerLevelAccess worldPos, @Nullable Player player) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getCapability(KEY)
                    .map(key -> key.canUnlock(lock, worldPos, player) && lock.canUnlock(key, worldPos, player))
                    .orElse(false)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onUnlock(ILock lock, ContainerLevelAccess worldPos, @Nullable Player player) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getCapability(KEY).filter(key -> lock.canUnlock(key, worldPos, player)).map(key -> {
                key.onUnlock(lock, worldPos, player);
                return true;
            }).orElse(false)) { return; }
        }
    }
}
