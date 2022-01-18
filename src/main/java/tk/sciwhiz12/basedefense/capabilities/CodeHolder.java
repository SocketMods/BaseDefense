package tk.sciwhiz12.basedefense.capabilities;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.api.ITooltipInfo;
import tk.sciwhiz12.basedefense.api.capablities.ICodeHolder;
import tk.sciwhiz12.basedefense.api.capablities.IContainsCode;
import tk.sciwhiz12.basedefense.util.Util;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;

/**
 * Default implementation of {@link ICodeHolder} and
 * {@link IContainsCode}. Can be extended
 * by other classes as a base class.
 *
 * @author SciWhiz12
 */
public class CodeHolder implements ICodeHolder, ITooltipInfo {
    protected LongList storedCodes = new LongArrayList();

    @Override
    public boolean containsCode(@Nullable Long code) {
        return code != null && storedCodes.contains(checkNotNull(code).longValue());
    }

    @Override
    public List<Long> getCodes() {
        return ImmutableList.copyOf(storedCodes);
    }

    @Override
    public void setCodes(List<Long> codes) {
        checkNotNull(codes);
        storedCodes.clear();
        storedCodes.addAll(codes);
    }

    @Override
    public void addCode(Long code) {
        storedCodes.add(checkNotNull(code).longValue());
    }

    @Override
    public void removeCode(Long code) {
        storedCodes.rem(code);
    }

    @Override
    public void addInformation(List<Component> info, boolean verbose) {
        if (!verbose || this.storedCodes.size() == 0) {
            info.add(Util.createAmountTooltip("tooltip.basedefense.codes.count", this.storedCodes.size()).withStyle(GRAY));
        } else {
            info.add(new TranslatableComponent("tooltip.basedefense.codes.header").withStyle(GRAY));
            for (long id : this.storedCodes) {
                info.add(new TranslatableComponent("tooltip.basedefense.codes.line", String.format("%016X", id))
                    .withStyle(DARK_GRAY));
            }
        }
    }
}
