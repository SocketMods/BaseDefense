package tk.sciwhiz12.basedefense.item.lock;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tk.sciwhiz12.basedefense.api.ITooltipInfo;
import tk.sciwhiz12.basedefense.block.LockedDoorBlock;
import tk.sciwhiz12.basedefense.item.IColorable;
import tk.sciwhiz12.basedefense.util.ItemHelper;

import java.util.List;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;

public abstract class AbstractLockCoreItem extends Item implements IColorable {
    public AbstractLockCoreItem(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        stack.getCapability(LOCK).filter(ITooltipInfo.class::isInstance)
                .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        if (!flagIn.isAdvanced()) { return; }
        ItemHelper.addColorInformation(stack, tooltip);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return world.getBlockState(pos).getBlock() instanceof LockedDoorBlock;
    }

    @Override
    public abstract ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt);
}
