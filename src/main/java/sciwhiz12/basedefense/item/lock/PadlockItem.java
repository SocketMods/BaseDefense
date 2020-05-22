package sciwhiz12.basedefense.item.lock;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import sciwhiz12.basedefense.api.lock.LockContext;

public class PadlockItem extends LockBaseItem {
    private static final IItemPropertyGetter COLOR_GETTER = (stack, world, livingEntity) -> {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) {
            return (float) tag.getIntArray("colors").length;
        }
        return 0.0F;
    };

    public PadlockItem() {
        super(new Item.Properties().maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("colors"), COLOR_GETTER);
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
