package sciwhiz12.basedefense.capabilities;

import static sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.util.INBTSerializable;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;

/**
 * An {@link ILock} and {@link ICodeHolder} that simply references the stored
 * {@link ItemStack}'s capabilities, if present.
 *
 * @author SciWhiz12
 */
public class CodedItemStackLock implements ICodeHolder, ILock, INBTSerializable<CompoundNBT> {
    private ItemStack lockStack = ItemStack.EMPTY;

    public void setStack(ItemStack stack) {
        this.lockStack = stack;
    }

    public ItemStack getStack() {
        return this.lockStack;
    }

    @Override
    public boolean canRemove(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
        return lockStack.getCapability(LOCK).map(lock -> lock.canRemove(key, worldPos, player)).orElse(false);
    }

    @Override
    public boolean canUnlock(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
        return lockStack.getCapability(LOCK).map(lock -> lock.canUnlock(key, worldPos, player)).orElse(false);
    }

    @Override
    public void onRemove(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
        lockStack.getCapability(LOCK).ifPresent(lock -> lock.onRemove(key, worldPos, player));
    }

    @Override
    public void onUnlock(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
        lockStack.getCapability(LOCK).ifPresent(lock -> lock.onUnlock(key, worldPos, player));
    }

    @Override
    public boolean containsCode(Long code) {
        return lockStack.getCapability(CODE_HOLDER).map(holder -> holder.containsCode(code)).orElse(false);
    }

    @Override
    public void setCodes(List<Long> codes) {
        if (codes == null) { throw new NullPointerException(); }
        lockStack.getCapability(CODE_HOLDER).ifPresent(holder -> holder.setCodes(codes));
    }

    @Override
    public List<Long> getCodes() {
        return lockStack.getCapability(CODE_HOLDER).map(ICodeHolder::getCodes).orElseGet(Collections::emptyList);
    }

    @Override
    public void addCode(Long code) {
        if (code == null) { throw new NullPointerException(); }
        lockStack.getCapability(CODE_HOLDER).ifPresent(holder -> holder.addCode(code));
    }

    @Override
    public void removeCode(Long code) {
        lockStack.getCapability(CODE_HOLDER).ifPresent(holder -> holder.removeCode(code));
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
