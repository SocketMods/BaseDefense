package sciwhiz12.basedefense.item.lock;

import javax.annotation.Nullable;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sciwhiz12.basedefense.api.lock.Decision;
import sciwhiz12.basedefense.api.lock.ILockable;
import sciwhiz12.basedefense.item.IColorable;

public class PadlockItem extends LockBaseItem implements IColorable {
    private static final IItemPropertyGetter COLOR_GETTER = (stack, world, livingEntity) -> {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) { return (float) tag.getIntArray("colors").length; }
        return 0.0F;
    };

    public PadlockItem() {
        super(new Item.Properties().maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("colors"), COLOR_GETTER);
    }

    @Override
    public Decision onUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos, ILockable block,
            @Nullable PlayerEntity player) {
        if (player.isSneaking()) {
            boolean flag = player.inventory.addItemStackToInventory(lockStack);
            if (flag && lockStack.isEmpty()) {
                lockStack.setCount(1);
                ItemEntity itementity1 = player.dropItem(lockStack, false);
                if (itementity1 != null) { itementity1.makeFakeItem(); }

                worldIn.playSound(
                    null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP,
                    SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F)
                            * 2.0F
                );
                player.container.detectAndSendChanges();
            } else {
                ItemEntity itementity = player.dropItem(lockStack, false);
                if (itementity != null) {
                    itementity.setNoPickupDelay();
                    itementity.setOwnerId(player.getUniqueID());
                }
            }
            block.setLock(worldIn, pos, ItemStack.EMPTY);
            return Decision.SUPPRESS;
        }
        return Decision.CONTINUE;
    }

    @Override
    public boolean isUnlockAllowed(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos, ILockable block,
            PlayerEntity player) {
        return true;
    }
}
