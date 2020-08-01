package sciwhiz12.basedefense.capabilities;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;
import sciwhiz12.basedefense.api.ITooltipInfo;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;
import sciwhiz12.basedefense.api.capablities.IContainsCode;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.util.text.TextFormatting.DARK_GRAY;
import static net.minecraft.util.text.TextFormatting.GRAY;

/**
 * An implementation of {@link IKey} and {@link ICodeHolder}.
 * <p>
 * Used as the default implementation of the {@code IKey} capability. Can be
 * used as a base class.
 *
 * @author SciWhiz12
 */
public class CodedKey implements ICodeHolder, IKey, INBTSerializable<LongNBT>, ITooltipInfo {
    protected Long storedCode = null;

    @Override
    public boolean canUnlock(ILock lock, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        if (lock instanceof IContainsCode) {
            IContainsCode codeLock = (IContainsCode) lock;
            return codeLock.containsCode(storedCode);
        }
        return false;
    }

    @Override
    public void onUnlock(ILock lock, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {}

    @Override
    public boolean containsCode(Long code) {
        return code != null && storedCode != null && code.longValue() == storedCode.longValue();
    }

    @Override
    public List<Long> getCodes() {
        return storedCode != null ? ImmutableList.of(storedCode) : ImmutableList.of();
    }

    /**
     * <strong>Impl. Note: Only takes the first entry in the list.</strong><br/>
     * {@inheritDoc}
     */
    @Override
    public void setCodes(List<Long> codes) {
        checkNotNull(codes);
        this.storedCode = codes.size() > 0 ? codes.get(0) : null;
    }

    /**
     * <strong>Impl. Note: Overwrites the currently stored code</strong><br/>
     * {@inheritDoc}
     */
    @Override
    public void addCode(Long code) {
        this.storedCode = checkNotNull(code);
    }

    @Override
    public void removeCode(Long code) {
        if (this.storedCode.longValue() == checkNotNull(code).longValue()) { this.storedCode = null; }
    }

    @Override
    public LongNBT serializeNBT() {
        return this.storedCode != null ? LongNBT.valueOf(this.storedCode) : null;
    }

    @Override
    public void deserializeNBT(LongNBT nbt) {
        this.storedCode = nbt.getLong();
    }

    @Override
    public void addInformation(List<ITextComponent> info, boolean verbose) {
        if (verbose && this.storedCode != null) {
            info.add(new TranslationTextComponent("tooltip.basedefense.codes.header").mergeStyle(GRAY));
            info.add(new TranslationTextComponent("tooltip.basedefense.codes.line", String.format("%016X", storedCode))
                    .mergeStyle(DARK_GRAY));
        } else {
            if (this.storedCode != null) {
                info.add(new TranslationTextComponent("tooltip.basedefense.codes.count.one", 1).mergeStyle(GRAY));
            } else {
                info.add(new TranslationTextComponent("tooltip.basedefense.codes.count.zero", 0).mergeStyle(GRAY));
            }
        }
    }
}
