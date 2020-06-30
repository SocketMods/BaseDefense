package sciwhiz12.basedefense.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A flexible {@link IStorage} for capabilities.
 * <p>
 * Checks if the given capability is a subclass of {@link INBTSerializable}, and
 * if so, passes the {@link INBT} to the instance.
 * 
 * @param <T> The capability type
 * @author SciWhiz12
 */
public class FlexibleStorage<T> implements IStorage<T> {
    @SuppressWarnings("unchecked")
    @Override
    public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
        if (instance instanceof INBTSerializable) {
            return ((INBTSerializable<INBT>) instance).serializeNBT();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
        if (instance instanceof INBTSerializable) { ((INBTSerializable<INBT>) instance).deserializeNBT(nbt); }
    }
}
