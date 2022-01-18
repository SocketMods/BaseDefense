package tk.sciwhiz12.basedefense.capabilities;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

/**
 * <p>A generic capability provider. Provides multiple capability instances from
 * one capability factory.</p>
 *
 * <p>If a capability is requested, and that capability matches one of those passed
 * into the constructor, then this returns the casted {@link LazyOptional} of
 * the single capability instance.</p>
 *
 * @param <C> The capability type
 * @author SciWhiz12
 */
public class GenericCapabilityProvider<C> implements ICapabilityProvider {
    private final Capability<C>[] capObjs;
    private final LazyOptional<C> capInst;

    @SafeVarargs
    public GenericCapabilityProvider(NonNullSupplier<C> factory, Capability<C>... cap) {
        this.capObjs = cap;
        this.capInst = LazyOptional.of(factory);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        for (Capability<C> capO : capObjs) { if (capO == cap) { return capInst.cast(); } }
        return LazyOptional.empty();
    }
}
