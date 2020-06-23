package sciwhiz12.basedefense.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import sciwhiz12.basedefense.Reference.TileEntities;
import sciwhiz12.basedefense.item.IColorable;

public class LockedDoorTile extends LockableTile {
    public static final ModelProperty<Integer> COLOR_PROPERTY = new ModelProperty<>();

    public LockedDoorTile() {
        super(TileEntities.LOCKED_DOOR);
    }

    public boolean hasColors() {
        ItemStack lock = this.getLockStack();
        return lock.getItem() instanceof IColorable && ((IColorable) lock.getItem()).hasColors(lock);
    }

    public int[] getColors() {
        ItemStack lock = this.getLockStack();
        if (lock.getItem() instanceof IColorable) { return ((IColorable) lock.getItem()).getColors(lock); }
        return new int[0];
    }

    @Override
    public IModelData getModelData() {
        IModelData data = new ModelDataMap.Builder().withInitial(COLOR_PROPERTY, 0).build();
        data.setData(COLOR_PROPERTY, this.getColors().length);
        return data;
    }
}
