package sciwhiz12.basedefense.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import sciwhiz12.basedefense.init.ModTileEntities;
import sciwhiz12.basedefense.item.IColorable;

public class LockedDoorTile extends LockableTile {
    public static final ModelProperty<Integer> COLOR_PROPERTY = new ModelProperty<>();

    public LockedDoorTile() {
        super(ModTileEntities.LOCKED_DOOR.get());
    }

    public boolean hasColors() {
        return lock.getItem() instanceof IColorable && ((IColorable) lock.getItem()).hasColors(lock);
    }

    public int[] getColors() {
        if (lock.getItem() instanceof IColorable) { return ((IColorable) lock.getItem()).getColors(lock); }
        return new int[0];
    }

    @Override
    public void setLock(ItemStack stack) {
        super.setLock(stack);
        this.requestModelDataUpdate();
    }

    @Override
    public IModelData getModelData() {
        IModelData data = new ModelDataMap.Builder().withInitial(COLOR_PROPERTY, 0).build();
        data.setData(COLOR_PROPERTY, this.getColors().length);
        return data;
    }
}
