package tk.sciwhiz12.basedefense.item.lock;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemHandlerHelper;
import tk.sciwhiz12.basedefense.api.capablities.IKey;
import tk.sciwhiz12.basedefense.capabilities.CodedLock;
import tk.sciwhiz12.basedefense.capabilities.SerializableCapabilityProvider;
import tk.sciwhiz12.basedefense.tileentity.LockableTile;
import tk.sciwhiz12.basedefense.util.ItemHelper;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.CONTAINS_CODE;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static tk.sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class CodedPadlockItem extends AbstractPadlockItem {
    public CodedPadlockItem() {
        super(new Properties().durability(0).tab(ITEM_GROUP));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new SerializableCapabilityProvider<>(() -> new CodedLock() {
            @Override
            public void onRemove(IKey key, ContainerLevelAccess worldPos, Player player) {
                worldPos.execute((world, pos) -> {
                    BlockEntity te = world.getBlockEntity(pos);
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
    public CompoundTag getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, ItemHelper.CapabilitySerializer.CODED_LOCK);
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundTag nbt) {
        ItemHelper.readItemShareTag(stack, nbt, ItemHelper.CapabilitySerializer.CODED_LOCK);
    }
}
