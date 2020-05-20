package sciwhiz12.basedefense.item.lock;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import sciwhiz12.basedefense.LockingUtil;
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.ILockable;
import sciwhiz12.basedefense.api.lock.LockContext;

public class KeyItem extends Item implements IKey {
    public KeyItem() {
        super(new Item.Properties().maxDamage(0));
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos,
            PlayerEntity player) {
        TileEntity te = world.getTileEntity(pos);
        return te != null && te instanceof ILockable;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn,
            List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!flagIn.isAdvanced()) return;
        long id = LockingUtil.getKeyID(stack);
        tooltip.add(
            new TranslationTextComponent("tooltip.basedefense.keyid", Long.toHexString(id))
                .applyTextStyle(TextFormatting.GRAY)
        );
    }

    @Override
    public boolean canUnlock(LockContext context) {
        return LockingUtil.hasUnlockID(context.getLockItem(), context.getKeyItem());
    }

    @Override
    public boolean unlock(LockContext context) {
        return true;
    }
}
