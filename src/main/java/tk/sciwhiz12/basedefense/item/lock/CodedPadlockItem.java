package tk.sciwhiz12.basedefense.item.lock;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemHandlerHelper;
import tk.sciwhiz12.basedefense.api.capablities.IKey;
import tk.sciwhiz12.basedefense.capabilities.CodedLock;
import tk.sciwhiz12.basedefense.capabilities.SerializableCapabilityProvider;
import tk.sciwhiz12.basedefense.tileentity.LockableTile;
import tk.sciwhiz12.basedefense.util.ItemHelper;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.*;
import static tk.sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class CodedPadlockItem extends AbstractPadlockItem {
    public CodedPadlockItem() {
        super(new Properties().durability(0).tab(ITEM_GROUP));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new SerializableCapabilityProvider<>(() -> new CodedLock() {
            @Override
            public void onRemove(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
                worldPos.execute((world, pos) -> {
                    TileEntity te = world.getBlockEntity(pos);
                    if (te instanceof LockableTile) {
                        LockableTile lockTile = (LockableTile) te;
                        ItemHandlerHelper.giveItemToPlayer(player, lockTile.getLockStack());
                        lockTile.setLockStack(ItemStack.EMPTY);
                    }
                });
            }
        }, CONTAINS_CODE, CODE_HOLDER, LOCK);
    }

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, CODE_HOLDER);
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        ItemHelper.readItemShareTag(stack, nbt, CODE_HOLDER);
    }
}
