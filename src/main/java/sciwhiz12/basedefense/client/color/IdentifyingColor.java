package sciwhiz12.basedefense.client.color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class IdentifyingColor implements IItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex > 0) {
            CompoundNBT tag = stack.getChildTag("display");
            if (tag != null && tag.contains("colors")) {
                int[] colors = tag.getIntArray("colors");
                if (colors.length > 0 && tintIndex == 1) {
                    return colors[0];
                } else if (colors.length > 1 && tintIndex == 2) {
                    return colors[1];
                }
            }
            return 0xffd5c000;
        }
        return 0xFFFFFFFF;
    }
}
