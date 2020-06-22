package sciwhiz12.basedefense.capabilities;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;

/**
 * Default implementation of {@link ICodeHolder} and
 * {@link sciwhiz12.basedefense.api.capablities.IContainsCode}. Can be extended
 * by other classes as a base class.
 * 
 * @author SciWhiz12
 */
public class CodeHolder implements ICodeHolder {
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
}
