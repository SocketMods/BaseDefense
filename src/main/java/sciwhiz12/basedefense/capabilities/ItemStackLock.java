package sciwhiz12.basedefense.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.util.INBTSerializable;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;

import javax.annotation.Nullable;

import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;

/**
 * An {@link ILock} that simply references the stored
 * {@link ItemStack}'s capabilities, if present.
 *
 * @author SciWhiz12
 */
public class ItemStackLock implements ILock, INBTSerializable<CompoundNBT> {
    protected ItemStack lockStack = ItemStack.EMPTY;

    public void setStack(ItemStack stack) {
        this.lockStack = stack;
    }

    public ItemStack getStack() {
        return this.lockStack;
    }

    @Override
    public boolean canRemove(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        return !lockStack.isEmpty() && lockStack.getCapability(LOCK).map(lock -> lock.canRemove(key, worldPos, player))
                .orElse(false);
    }

    @Override
    public boolean canUnlock(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        return !lockStack.isEmpty() && lockStack.getCapability(LOCK).map(lock -> lock.canUnlock(key, worldPos, player))
                .orElse(false);
    }

    @Override
    public void onRemove(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        if (!lockStack.isEmpty()) lockStack.getCapability(LOCK).ifPresent(lock -> lock.onRemove(key, worldPos, player));
    }

    @Override
    public void onUnlock(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        if (!lockStack.isEmpty()) lockStack.getCapability(LOCK).ifPresent(lock -> lock.onUnlock(key, worldPos, player));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return this.lockStack.write(new CompoundNBT());
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.lockStack = ItemStack.read(nbt);
    }
}
