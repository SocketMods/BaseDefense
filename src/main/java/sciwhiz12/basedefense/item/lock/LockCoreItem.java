package sciwhiz12.basedefense.item.lock;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sciwhiz12.basedefense.api.lock.Decision;
import sciwhiz12.basedefense.api.lock.ILockable;

public class LockCoreItem extends LockBaseItem {
    private static final IItemPropertyGetter COLOR_GETTER = (stack, world, livingEntity) -> {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) {
            return (float) tag.getIntArray("colors").length;
        }
        return 0.0F;
    };

    public LockCoreItem() {
        super(new Item.Properties().maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("colors"), COLOR_GETTER);
    }

    @Override
    public Decision onUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos,
            ILockable block, @Nullable PlayerEntity player) {
        return Decision.CONTINUE;
    }

    @Override
    public boolean isUnlockAllowed(ItemStack lockStack, ItemStack keyStack, World worldIn,
            BlockPos pos, ILockable block, PlayerEntity player) {
        return true;
    }
}
