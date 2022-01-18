package tk.sciwhiz12.basedefense.capabilities;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraftforge.common.util.INBTSerializable;
import tk.sciwhiz12.basedefense.api.capablities.IKey;
import tk.sciwhiz12.basedefense.api.capablities.ILock;

import javax.annotation.Nullable;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;

/**
 * An {@link ILock} that simply references the stored
 * {@link ItemStack}'s capabilities, if present.
 *
 * @author SciWhiz12
 */
public class ItemStackLock implements ILock, INBTSerializable<CompoundTag> {
    protected ItemStack lockStack = ItemStack.EMPTY;

    public void setStack(ItemStack stack) {
        this.lockStack = stack;
    }

    public ItemStack getStack() {
        return this.lockStack;
    }

    @Override
    public boolean canRemove(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {
        return !lockStack.isEmpty() && lockStack.getCapability(LOCK).map(lock -> lock.canRemove(key, worldPos, player))
                .orElse(false);
    }

    @Override
    public boolean canUnlock(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {
        return !lockStack.isEmpty() && lockStack.getCapability(LOCK).map(lock -> lock.canUnlock(key, worldPos, player))
                .orElse(false);
    }

    @Override
    public void onRemove(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {
        if (!lockStack.isEmpty()) lockStack.getCapability(LOCK).ifPresent(lock -> lock.onRemove(key, worldPos, player));
    }

    @Override
    public void onUnlock(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {
        if (!lockStack.isEmpty()) lockStack.getCapability(LOCK).ifPresent(lock -> lock.onUnlock(key, worldPos, player));
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.lockStack.save(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.lockStack = ItemStack.of(nbt);
    }
}
