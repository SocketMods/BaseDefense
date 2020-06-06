package sciwhiz12.basedefense.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class GenericCapabilityProvider<T extends INBT, C extends INBTSerializable<T>> implements ICapabilitySerializable<T> {
    private final Capability<C> capObject;
    private final LazyOptional<C> instance;

    public GenericCapabilityProvider(Capability<C> cap, NonNullSupplier<C> factory) {
        this.capObject = cap;
        this.instance = LazyOptional.of(factory);
    }

    @Override
    public <X> LazyOptional<X> getCapability(Capability<X> cap, Direction side) {
        return cap == capObject ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public T serializeNBT() {
        return instance.orElseThrow(IllegalStateException::new).serializeNBT();
    }

    @Override
    public void deserializeNBT(T nbt) {
        instance.orElseThrow(IllegalStateException::new).deserializeNBT(nbt);
    }
}
