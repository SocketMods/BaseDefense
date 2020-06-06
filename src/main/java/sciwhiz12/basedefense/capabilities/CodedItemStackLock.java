package sciwhiz12.basedefense.capabilities;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.IWorldPosCallable;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.init.ModCapabilities;

public class CodedItemStackLock extends CodedLock {
    private ItemStack lockStack = ItemStack.EMPTY;

    public void setStack(ItemStack stack) {
        this.lockStack = stack;
    }

    public ItemStack getStack() {
        return this.lockStack;
    }

    @Override
    public boolean canRemove(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
        return lockStack.getCapability(ModCapabilities.LOCK).map((lock) -> lock.canRemove(key, worldPos, player)).orElse(
            false);
    }

    @Override
    public boolean canUnlock(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
        return lockStack.getCapability(ModCapabilities.LOCK).map((lock) -> lock.canUnlock(key, worldPos, player)).orElse(
            false);
    }

    @Override
    public void onRemove(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
        lockStack.getCapability(ModCapabilities.LOCK).ifPresent((lock) -> { lock.onRemove(key, worldPos, player); });
    }

    @Override
    public void onUnlock(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
        lockStack.getCapability(ModCapabilities.LOCK).ifPresent((lock) -> { lock.onUnlock(key, worldPos, player); });
    }

    public List<Long> getCodes() {
        return lockStack.getCapability(ModCapabilities.LOCK).filter((lock) -> lock instanceof CodedLock).map((
                lock) -> ((CodedLock) lock).getCodes()).orElse(Collections.emptyList());
    }

    public boolean containsCode(long code) {
        return lockStack.getCapability(ModCapabilities.LOCK).filter((lock) -> lock instanceof CodedLock).map((
                lock) -> ((CodedLock) lock).containsCode(code)).orElse(false);
    }

    public void addCode(long code) {
        lockStack.getCapability(ModCapabilities.LOCK).filter((lock) -> lock instanceof CodedLock).ifPresent((
                lock) -> ((CodedLock) lock).addCode(code));
    }

    @Override
    public void removeCode(long code) {
        lockStack.getCapability(ModCapabilities.LOCK).filter((lock) -> lock instanceof CodedLock).ifPresent((
                lock) -> ((CodedLock) lock).removeCode(code));
    }

    @Override
    public INBT serializeNBT() {
        return this.lockStack.write(new CompoundNBT());
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        if (nbt instanceof CompoundNBT) { this.lockStack = ItemStack.read((CompoundNBT) nbt); }
    }
}
