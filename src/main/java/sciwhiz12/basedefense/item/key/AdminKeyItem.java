package sciwhiz12.basedefense.item.key;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import sciwhiz12.basedefense.api.ITooltipInfo;
import sciwhiz12.basedefense.capabilities.AdminKeyLock;
import sciwhiz12.basedefense.capabilities.GenericCapabilityProvider;

import java.util.List;

import static sciwhiz12.basedefense.Reference.Capabilities.KEY;
import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class AdminKeyItem extends Item {
    public AdminKeyItem() {
        super(new Item.Properties().maxDamage(0).rarity(Rarity.EPIC).group(ITEM_GROUP));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        stack.getCapability(KEY).filter(ITooltipInfo.class::isInstance)
                .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
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
        return new GenericCapabilityProvider<>(AdminKeyLock::new, KEY);
    }
}
