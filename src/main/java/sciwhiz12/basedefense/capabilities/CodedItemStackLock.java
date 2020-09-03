package sciwhiz12.basedefense.capabilities;

import sciwhiz12.basedefense.api.capablities.ICodeHolder;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;

/**
 * An {@link sciwhiz12.basedefense.api.capablities.ILock ILock} and {@link ICodeHolder}
 * that simply references the stored {@link net.minecraft.item.ItemStack ItemStack's} capabilities, if present.
 *
 * @author SciWhiz12
 */
public class CodedItemStackLock extends ItemStackLock implements ICodeHolder {
    @Override
    public boolean containsCode(Long code) {
        return !lockStack.isEmpty() && lockStack.getCapability(CODE_HOLDER).map(holder -> holder.containsCode(code))
                .orElse(false);
    }

    @Override
    public void setCodes(List<Long> codes) {
        checkNotNull(codes);
        if (!lockStack.isEmpty()) lockStack.getCapability(CODE_HOLDER).ifPresent(holder -> holder.setCodes(codes));
    }

    @Override
    public List<Long> getCodes() {
        return !lockStack.isEmpty() ?
                lockStack.getCapability(CODE_HOLDER).map(ICodeHolder::getCodes).orElseGet(Collections::emptyList) :
                Collections.emptyList();
    }

    @Override
    public void addCode(Long code) {
        checkNotNull(code);
        if (!lockStack.isEmpty()) lockStack.getCapability(CODE_HOLDER).ifPresent(holder -> holder.addCode(code));
    }

    @Override
    public void removeCode(Long code) {
        if (!lockStack.isEmpty()) lockStack.getCapability(CODE_HOLDER).ifPresent(holder -> holder.removeCode(code));
    }
}
