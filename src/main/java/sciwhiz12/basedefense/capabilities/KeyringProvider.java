package sciwhiz12.basedefense.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import sciwhiz12.basedefense.item.key.KeyItem;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
import static sciwhiz12.basedefense.Reference.Capabilities.KEY;

public class KeyringProvider implements ICapabilitySerializable<INBT> {
    private final ItemStackHandler item = createItemHandler();
    private final ItemHandlerKey key = new ItemHandlerKey(item);
    private final LazyOptional<ItemStackHandler> itemCap = LazyOptional.of(() -> item);
    private final LazyOptional<ItemHandlerKey> keyCap = LazyOptional.of(() -> key);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (ITEM_HANDLER_CAPABILITY != null && cap == ITEM_HANDLER_CAPABILITY) { return itemCap.cast(); }
        if (KEY != null && cap == KEY) { return keyCap.cast(); }
        return LazyOptional.empty();
    }

    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(9) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.getItem() instanceof KeyItem;
            }
        };
    }

    @Override
    public INBT serializeNBT() {
        return ITEM_HANDLER_CAPABILITY.writeNBT(item, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        ITEM_HANDLER_CAPABILITY.readNBT(item, null, nbt);
    }
}
