package tk.sciwhiz12.basedefense.item.lock;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
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
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        stack.getCapability(LOCK).filter(ITooltipInfo.class::isInstance)
                .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        if (!flagIn.isAdvanced()) { return; }
        ItemHelper.addColorInformation(stack, tooltip);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return world.getBlockState(pos).getBlock() instanceof LockedDoorBlock;
    }

    @Override
    public abstract ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt);
}
