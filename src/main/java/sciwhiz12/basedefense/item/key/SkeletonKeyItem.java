package sciwhiz12.basedefense.item.key;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.ILockable;
import sciwhiz12.basedefense.init.ModItems;

public class SkeletonKeyItem extends Item implements IKey {
    public SkeletonKeyItem() {
        super(new Item.Properties().maxDamage(0).rarity(Rarity.EPIC).group(ModItems.GROUP));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tooltip.basedefense.skeleton_key").applyTextStyle(TextFormatting.RED));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return world.isBlockLoaded(pos) && world.getBlockState(pos).getBlock() instanceof ILockable;
    }

    @Override
    public boolean canUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos, ILockable block,
            @Nullable PlayerEntity player) {
        return true;
    }

    @Override
    public void onUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos, ILockable block,
            @Nullable PlayerEntity player) {
        if (player != null && player.isSneaking() && block.hasLock(worldIn, pos)) {
            ItemHandlerHelper.giveItemToPlayer(player, lockStack);
            block.setLock(worldIn, pos, ItemStack.EMPTY);
        }
    }
}
