package sciwhiz12.basedefense.capabilities;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import sciwhiz12.basedefense.api.ITooltipInfo;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;
import sciwhiz12.basedefense.util.Util;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.util.text.TextFormatting.DARK_GRAY;
import static net.minecraft.util.text.TextFormatting.GRAY;

/**
 * Default implementation of {@link ICodeHolder} and
 * {@link sciwhiz12.basedefense.api.capablities.IContainsCode}. Can be extended
 * by other classes as a base class.
 *
 * @author SciWhiz12
 */
public class CodeHolder implements ICodeHolder, ITooltipInfo {
    protected LongList storedCodes = new LongArrayList();

    @Override
    public boolean containsCode(Long code) {
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
    public void addInformation(List<ITextComponent> info, boolean verbose) {
        if (!verbose || this.storedCodes.size() == 0) {
            info.add(Util.createAmountTooltip("tooltip.basedefense.codes.count", this.storedCodes.size()).withStyle(GRAY));
        } else {
            info.add(new TranslationTextComponent("tooltip.basedefense.codes.header").withStyle(GRAY));
            for (long id : this.storedCodes) {
                info.add(new TranslationTextComponent("tooltip.basedefense.codes.line", String.format("%016X", id))
                        .withStyle(DARK_GRAY));
            }
        }
    }
}
