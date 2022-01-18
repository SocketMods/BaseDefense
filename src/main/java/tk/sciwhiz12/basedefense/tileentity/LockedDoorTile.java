package tk.sciwhiz12.basedefense.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import tk.sciwhiz12.basedefense.Reference.TileEntities;
import tk.sciwhiz12.basedefense.item.IColorable;

public class LockedDoorTile extends LockableTile {
    public static final ModelProperty<Integer> COLOR_PROPERTY = new ModelProperty<>();

    public LockedDoorTile(BlockPos pos, BlockState state) {
        super(TileEntities.LOCKED_DOOR, pos, state);
    }

    public boolean hasColors() {
        ItemStack lock = this.getLockStack();
        return lock.getItem() instanceof IColorable color && color.hasColors(lock);
    }

    public int[] getColors() {
        ItemStack lock = this.getLockStack();
        if (lock.getItem() instanceof IColorable color) {
            return color.getColors(lock);
        }
        return new int[0];
    }

    @NonNull
    @Override
    public IModelData getModelData() {
        IModelData data = new ModelDataMap.Builder().withInitial(COLOR_PROPERTY, 0).build();
        data.setData(COLOR_PROPERTY, this.getColors().length);
        return data;
    }
}
