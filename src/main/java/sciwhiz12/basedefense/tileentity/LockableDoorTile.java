package sciwhiz12.basedefense.tileentity;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import sciwhiz12.basedefense.api.lock.LockContext;
import sciwhiz12.basedefense.init.ModTileEntities;

public class LockableDoorTile extends LockableTile {
    public LockableDoorTile() {
        super(ModTileEntities.LOCK_DOOR_TILE.get());
    }

    @Override
    public boolean onUnlock(LockContext ctx) {
        if (ctx.getPlayer().isSneaking()) {
            ItemStack lock = ctx.getLockItem();
            ServerPlayerEntity player = (ServerPlayerEntity) ctx.getPlayer();
            boolean flag = player.inventory.addItemStackToInventory(lock);
            if (flag && lock.isEmpty()) {
                lock.setCount(1);
                ItemEntity itementity1 = player.dropItem(lock, false);
                if (itementity1 != null) { itementity1.makeFakeItem(); }

                ctx.getWorld().playSound(
                    (PlayerEntity) null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG()
                        .nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F
                );
                player.container.detectAndSendChanges();
            } else {
                ItemEntity itementity = player.dropItem(lock, false);
                if (itementity != null) {
                    itementity.setNoPickupDelay();
                    itementity.setOwnerId(player.getUniqueID());
                }
            }
            ctx.getLockable().setLock(ItemStack.EMPTY);
            return false;
        }
        return true;
    }
}
