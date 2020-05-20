package sciwhiz12.basedefense.item.lock;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.ILockable;
import sciwhiz12.basedefense.api.lock.LockContext;
import sciwhiz12.basedefense.init.ModItems;

public class SkeletonKeyItem extends Item implements IKey {
    public SkeletonKeyItem() {
        super(new Item.Properties().maxDamage(0).rarity(Rarity.EPIC).group(ModItems.GROUP));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn,
            List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(
            new TranslationTextComponent("tooltip.basedefense.skeleton_key").applyTextStyle(
                TextFormatting.RED
            )
        );
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos,
            PlayerEntity player) {
        TileEntity te = world.getTileEntity(pos);
        return te != null && te instanceof ILockable;
    }

    @Override
    public boolean canUnlock(LockContext context) {
        return true;
    }

    @Override
    public boolean unlock(LockContext ctx) {
        if (ctx.getPlayer().isSneaking() && ctx.getLockable().hasLock()) {
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
