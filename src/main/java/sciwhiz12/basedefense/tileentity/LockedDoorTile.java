package sciwhiz12.basedefense.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants;
import sciwhiz12.basedefense.init.ModTileEntities;

public class LockedDoorTile extends LockableTile {
    public static final ModelProperty<Integer> COLOR_PROPERTY = new ModelProperty<>();

    public LockedDoorTile() {
        super(ModTileEntities.LOCKED_DOOR.get());
    }

    public boolean hasColors() {
        CompoundNBT tag = this.lock.getTag();
        return this.hasLock() && this.lock.hasTag() && tag.contains("display", Constants.NBT.TAG_COMPOUND) && tag
            .getCompound("display").contains("colors");
    }

    public int[] getColors() {
        return this.hasColors() ? this.lock.getTag().getCompound("display").getIntArray("colors") : new int[0];
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
