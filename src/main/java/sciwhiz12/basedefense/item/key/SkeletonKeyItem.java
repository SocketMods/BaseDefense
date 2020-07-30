package sciwhiz12.basedefense.item.key;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;
import sciwhiz12.basedefense.capabilities.GenericCapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

import static sciwhiz12.basedefense.Reference.Capabilities.KEY;
import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class SkeletonKeyItem extends Item {
    public SkeletonKeyItem() {
        super(new Item.Properties().maxDamage(0).rarity(Rarity.EPIC).group(ITEM_GROUP));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tooltip.basedefense.skeleton_key").mergeStyle(TextFormatting.DARK_RED));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        TileEntity te = world.getTileEntity(pos);
        return te != null && te.getCapability(LOCK).isPresent();
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new GenericCapabilityProvider<>(() -> new IKey() {
            @Override
            public boolean canUnlock(ILock lock, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
                return true;
            }

            @Override
            public void onUnlock(ILock lock, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player) {}
        }, KEY);
    }
}
