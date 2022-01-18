package tk.sciwhiz12.basedefense.item.key;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tk.sciwhiz12.basedefense.api.ITooltipInfo;
import tk.sciwhiz12.basedefense.capabilities.CodedKey;
import tk.sciwhiz12.basedefense.capabilities.SerializableCapabilityProvider;
import tk.sciwhiz12.basedefense.item.IColorable;
import tk.sciwhiz12.basedefense.util.ItemHelper;

import java.util.List;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.CONTAINS_CODE;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.KEY;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static tk.sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class KeyItem extends Item implements IColorable {
    public KeyItem() {
        super(new Item.Properties().durability(0).tab(ITEM_GROUP));
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        BlockEntity tile = world.getBlockEntity(pos);
        return tile != null && tile.getCapability(LOCK).isPresent();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        stack.getCapability(KEY).filter(ITooltipInfo.class::isInstance)
            .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        if (!flagIn.isAdvanced()) return;
        ItemHelper.addColorInformation(stack, tooltip);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new SerializableCapabilityProvider<>(CodedKey::new, KEY, CONTAINS_CODE, CODE_HOLDER);
    }

    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, ItemHelper.CapabilitySerializer.CODED_KEY);
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundTag nbt) {
        ItemHelper.readItemShareTag(stack, nbt, ItemHelper.CapabilitySerializer.CODED_KEY);
    }
}
