package tk.sciwhiz12.basedefense.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper methods for {@link net.minecraft.world.Container}s.
 *
 * @author SciWhiz12
 */
public final class ContainerHelper {
    // Prevent instantiation
    private ContainerHelper() {
    }

    public static int addSlotRange(Consumer<Slot> adder, IItemHandler handler, int index, int x, int y, int amount, int dx) {
        checkNotNull(adder);
        checkNotNull(handler);
        for (int i = 0; i < amount; i++) {
            adder.accept(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    public static int addSlotBox(Consumer<Slot> adder, IItemHandler handler, int index, int x, int y, int horAmount, int dx,
                                 int verAmount, int dy) {
        checkNotNull(adder);
        checkNotNull(handler);
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(adder, handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    public static void layoutPlayerInventorySlots(Consumer<Slot> adder, Inventory playerInv, int leftCol, int topRow) {
        checkNotNull(adder);
        checkNotNull(playerInv);
        IItemHandler inv = new InvWrapper(playerInv);
        addSlotBox(adder, inv, 9, leftCol, topRow, 9, 18, 3, 18);
        topRow += 58;
        addSlotRange(adder, inv, 0, leftCol, topRow, 9, 18);
    }
}
