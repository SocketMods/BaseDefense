package tk.sciwhiz12.basedefense.item.lock;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemHandlerHelper;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.api.capablities.IKey;
import tk.sciwhiz12.basedefense.capabilities.AdminKeyLock;
import tk.sciwhiz12.basedefense.capabilities.GenericCapabilityProvider;
import tk.sciwhiz12.basedefense.tileentity.LockableTile;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static tk.sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class AdminPadlockItem extends AbstractPadlockItem {
    public AdminPadlockItem() {
        super(new Item.Properties().durability(0).rarity(Rarity.EPIC).tab(ITEM_GROUP));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new GenericCapabilityProvider<>(() -> new AdminKeyLock() {
            @Override
            public void onRemove(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {
                super.onRemove(key, worldPos, player);
                worldPos.execute((world, pos) -> {
                    @Nullable BlockEntity te = world.getBlockEntity(pos);
                    if (te instanceof LockableTile) {
                        LockableTile lockTile = (LockableTile) te;
                        ItemHandlerHelper.giveItemToPlayer(player, lockTile.getLockStack());
                        lockTile.setLockStack(ItemStack.EMPTY);
                    }
                });
            }
        }, LOCK);
    }
}
