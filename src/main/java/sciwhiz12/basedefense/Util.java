package sciwhiz12.basedefense;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class Util {
    public static IWorldPosCallable getOrDummy(@Nullable World world, @Nullable BlockPos pos) {
        if (world != null && pos != null) { return IWorldPosCallable.of(world, pos); }
        return IWorldPosCallable.DUMMY;
    }

    public static <T, R> R applyOrDefault(LazyOptional<T> opt, R defaultValue, Function<T, R> func) {
        AtomicReference<R> retVal = new AtomicReference<>(defaultValue);
        opt.ifPresent((obj) -> { retVal.set(func.apply(obj)); });
        return retVal.get();
    }
}
