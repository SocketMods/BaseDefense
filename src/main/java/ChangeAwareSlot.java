import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ChangeAwareSlot extends SlotItemHandler {
    private final ChangeCallback changeCallback;

    public ChangeAwareSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition,
            ChangeCallback changeCallback) {
        super(itemHandler, index, xPosition, yPosition);
        this.changeCallback = changeCallback;
    }

    @Override
    public void onSlotChange(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) {
        super.onSlotChange(oldStackIn, newStackIn);
        changeCallback.onSlotChange(this, oldStackIn, newStackIn);
    }

    public interface ChangeCallback {
        void onSlotChange(SlotItemHandler slot, ItemStack oldStack, ItemStack newStack);
    }
}
