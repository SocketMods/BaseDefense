package tk.sciwhiz12.basedefense.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A version of {@link GenericCapabilityProvider} that caters to capability
 * instances which are {@link INBTSerializable}.
 *
 * @param <C> The capability type
 * @param <N> The {@link Tag} type that the capability supports
 * @author SciWhiz12
 */
public class SerializableCapabilityProvider<C extends INBTSerializable<N>, N extends Tag>
    implements ICapabilitySerializable<N> {
    private final Capability<?>[] capObjs;
    private final LazyOptional<C> capInst;

    public SerializableCapabilityProvider(NonNullSupplier<C> factory, Capability<?>... caps) {
        this.capObjs = caps;
        this.capInst = LazyOptional.of(factory);
    }

    @NonNull
    @Override
    public <X> LazyOptional<X> getCapability(Capability<X> cap, @Nullable Direction side) {
        for (Capability<?> capO : capObjs) {
            if (capO == cap) {
                return capInst.cast();
            }
        }
        return LazyOptional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public N serializeNBT() {
        return capInst.map(INBTSerializable::serializeNBT).orElse((N) EndTag.INSTANCE);
    }

    @Override
    public void deserializeNBT(N nbt) {
        if (!(nbt instanceof EndTag)) {
            capInst.ifPresent((inst) -> inst.deserializeNBT(nbt));
        }
    }
}
