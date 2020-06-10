package sciwhiz12.basedefense.util;

import java.util.function.Consumer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public final class ContainerHelper {
    public static int addSlotRange(Consumer<Slot> adder, IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            adder.accept(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    public static int addSlotBox(Consumer<Slot> adder, IItemHandler handler, int index, int x, int y, int horAmount, int dx,
            int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(adder, handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    public static void layoutPlayerInventorySlots(Consumer<Slot> adder, PlayerInventory playerInv, int leftCol, int topRow) {
        IItemHandler inv = new InvWrapper(playerInv);
        addSlotBox(adder, inv, 9, leftCol, topRow, 9, 18, 3, 18);
        topRow += 58;
        addSlotRange(adder, inv, 0, leftCol, topRow, 9, 18);
    }
}
