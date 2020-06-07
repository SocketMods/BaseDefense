package sciwhiz12.basedefense.capabilities;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

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
