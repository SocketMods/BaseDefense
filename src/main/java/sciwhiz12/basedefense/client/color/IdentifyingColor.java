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
                /*
                 * if (colors.length > 0 && tintIndex == 2) { return colors[0]; } else if
                 * (colors.length > 1 && tintIndex == 3) { return colors[1]; } else if
                 * (colors.length > 2 && tintIndex == 4) { return colors[2]; }
                 */
            }
        }
        return -1;
    }
}
