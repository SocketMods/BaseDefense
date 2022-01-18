package tk.sciwhiz12.basedefense.item.key;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.api.ITooltipInfo;
import tk.sciwhiz12.basedefense.capabilities.AdminKeyLock;
import tk.sciwhiz12.basedefense.capabilities.GenericCapabilityProvider;

import java.util.List;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.KEY;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static tk.sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class AdminKeyItem extends Item {
    public AdminKeyItem() {
        super(new Item.Properties().durability(0).rarity(Rarity.EPIC).tab(ITEM_GROUP));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
                                TooltipFlag flagIn) {
        stack.getCapability(KEY).filter(ITooltipInfo.class::isInstance)
            .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        @Nullable BlockEntity te = world.getBlockEntity(pos);
        return te != null && te.getCapability(LOCK).isPresent();
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new GenericCapabilityProvider<>(AdminKeyLock::new, KEY);
    }
}
