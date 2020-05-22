package sciwhiz12.basedefense.client.color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class IdentifyingColor implements IItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex > 1) {
            CompoundNBT tag = stack.getChildTag("display");
            if (tag != null && tag.contains("colors")) {
                int[] colors = tag.getIntArray("colors");
                if (colors.length - tintIndex + 2 > 0) { return colors[tintIndex - 2]; }
            }
        }
        return -1;
    }
}
