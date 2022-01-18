package tk.sciwhiz12.basedefense.capabilities;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.LongTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraftforge.common.util.INBTSerializable;
import tk.sciwhiz12.basedefense.api.ITooltipInfo;
import tk.sciwhiz12.basedefense.api.capablities.ICodeHolder;
import tk.sciwhiz12.basedefense.api.capablities.IContainsCode;
import tk.sciwhiz12.basedefense.api.capablities.IKey;
import tk.sciwhiz12.basedefense.api.capablities.ILock;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;

/**
 * <p>An implementation of {@link IKey} and {@link ICodeHolder}.</p>
 *
 * <p>Used as the default implementation of the {@code IKey} capability. Can be
 * used as a base class.</p>
 *
 * @author SciWhiz12
 */
public class CodedKey implements ICodeHolder, IKey, INBTSerializable<LongTag>, ITooltipInfo {
    protected Long storedCode = null;

    @Override
    public boolean canUnlock(ILock lock, ContainerLevelAccess worldPos, @Nullable Player player) {
        if (lock instanceof IContainsCode) {
            IContainsCode codeLock = (IContainsCode) lock;
            return codeLock.containsCode(storedCode);
        }
        return false;
    }

    @Override
    public void onUnlock(ILock lock, ContainerLevelAccess worldPos, @Nullable Player player) {
    }

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
        if (this.storedCode.longValue() == checkNotNull(code).longValue()) {
            this.storedCode = null;
        }
    }

    @Override
    public LongTag serializeNBT() {
        return this.storedCode != null ? LongTag.valueOf(this.storedCode) : LongTag.valueOf(0);
    }

    @Override
    public void deserializeNBT(LongTag nbt) {
        this.storedCode = nbt.getAsLong() != 0 ? nbt.getAsLong() : null;
    }

    @Override
    public void addInformation(List<Component> info, boolean verbose) {
        if (verbose && this.storedCode != null) {
            info.add(new TranslatableComponent("tooltip.basedefense.codes.header").withStyle(GRAY));
            info.add(new TranslatableComponent("tooltip.basedefense.codes.line", String.format("%016X", storedCode))
                .withStyle(DARK_GRAY));
        } else {
            if (this.storedCode != null) {
                info.add(new TranslatableComponent("tooltip.basedefense.codes.count.one", 1).withStyle(GRAY));
            } else {
                info.add(new TranslatableComponent("tooltip.basedefense.codes.count.zero", 0).withStyle(GRAY));
            }
        }
    }
}
