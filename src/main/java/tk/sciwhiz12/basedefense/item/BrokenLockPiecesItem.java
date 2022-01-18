package tk.sciwhiz12.basedefense.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tk.sciwhiz12.basedefense.Reference;
import tk.sciwhiz12.basedefense.api.ITooltipInfo;
import tk.sciwhiz12.basedefense.capabilities.CodedLock;
import tk.sciwhiz12.basedefense.capabilities.SerializableCapabilityProvider;
import tk.sciwhiz12.basedefense.util.ItemHelper;

import java.util.List;

public class BrokenLockPiecesItem extends Item implements IColorable {
    public BrokenLockPiecesItem() {
        super(new Item.Properties().durability(0).tab(Reference.ITEM_GROUP));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (hasPreviousName(stack)) {
            tooltip.add(getPreviousName(stack).withStyle(ChatFormatting.ITALIC));
        }
        stack.getCapability(Reference.Capabilities.CODE_HOLDER).filter(ITooltipInfo.class::isInstance)
            .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        if (!flagIn.isAdvanced()) return;
        ItemHelper.addColorInformation(stack, tooltip);
    }

    public boolean hasPreviousName(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("BrokenLockName", Tag.TAG_STRING);
    }

    public MutableComponent getPreviousName(ItemStack stack) {
        return hasPreviousName(stack) ?
            Component.Serializer.fromJson(stack.getTag().getString("BrokenLockName")) :
            new TextComponent("");
    }

    public void setPreviousName(ItemStack stack, Component name) {
        stack.getOrCreateTag().putString("BrokenLockName", Component.Serializer.toJson(name));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        // Using CodedLock for code holder capability, not for lock capability
        return new SerializableCapabilityProvider<>(CodedLock::new, Reference.Capabilities.CONTAINS_CODE, Reference.Capabilities.CODE_HOLDER);
    }

    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, ItemHelper.CapabilitySerializer.CODED_LOCK);
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundTag nbt) {
        ItemHelper.readItemShareTag(stack, nbt, ItemHelper.CapabilitySerializer.CODED_LOCK);
    }
}
