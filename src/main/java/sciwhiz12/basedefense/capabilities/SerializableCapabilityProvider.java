package sciwhiz12.basedefense.capabilities;

import net.minecraft.nbt.EndNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class SerializableCapabilityProvider<C extends INBTSerializable<N>, N extends INBT> implements ICapabilitySerializable<N> {
    private final Capability<?>[] capObjs;
    private final LazyOptional<? extends C> capInst;

    @SafeVarargs
    public SerializableCapabilityProvider(NonNullSupplier<? extends C> factory, Capability<?>... caps) {
        this.capObjs = caps;
        this.capInst = LazyOptional.of(factory);
    }

    @Override
    public <X> LazyOptional<X> getCapability(Capability<X> cap, Direction side) {
        for (Capability<?> capO : capObjs) { if (capO == cap) { return capInst.cast(); } }
        return LazyOptional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public N serializeNBT() {
        C inst = capInst.orElse(null);
        N nbt = inst != null ? inst.serializeNBT() : null;
        return nbt != null ? nbt : (N) EndNBT.INSTANCE;
    }

    @Override
    public void deserializeNBT(N nbt) {
        if (!(nbt instanceof EndNBT)) capInst.ifPresent((inst) -> inst.deserializeNBT(nbt));
    }
}
