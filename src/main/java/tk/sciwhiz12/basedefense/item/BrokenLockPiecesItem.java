package tk.sciwhiz12.basedefense.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import tk.sciwhiz12.basedefense.api.ITooltipInfo;
import tk.sciwhiz12.basedefense.capabilities.CodedLock;
import tk.sciwhiz12.basedefense.capabilities.SerializableCapabilityProvider;
import tk.sciwhiz12.basedefense.util.ItemHelper;
import tk.sciwhiz12.basedefense.Reference;

import java.util.List;

public class BrokenLockPiecesItem extends Item implements IColorable {
    public BrokenLockPiecesItem() {
        super(new Item.Properties().durability(0).tab(Reference.ITEM_GROUP));
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (hasPreviousName(stack)) { tooltip.add(getPreviousName(stack).withStyle(TextFormatting.ITALIC)); }
        stack.getCapability(Reference.Capabilities.CODE_HOLDER).filter(ITooltipInfo.class::isInstance)
                .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        if (!flagIn.isAdvanced()) return;
        ItemHelper.addColorInformation(stack, tooltip);
    }

    public boolean hasPreviousName(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("BrokenLockName", Constants.NBT.TAG_STRING);
    }

    public IFormattableTextComponent getPreviousName(ItemStack stack) {
        return hasPreviousName(stack) ?
                ITextComponent.Serializer.fromJson(stack.getTag().getString("BrokenLockName")) :
                new StringTextComponent("");
    }

    public void setPreviousName(ItemStack stack, ITextComponent name) {
        stack.getOrCreateTag().putString("BrokenLockName", ITextComponent.Serializer.toJson(name));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        // Using CodedLock for code holder capability, not for lock capability
        return new SerializableCapabilityProvider<>(CodedLock::new, Reference.Capabilities.CONTAINS_CODE, Reference.Capabilities.CODE_HOLDER);
    }

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, Reference.Capabilities.CODE_HOLDER);
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        ItemHelper.readItemShareTag(stack, nbt, Reference.Capabilities.CODE_HOLDER);
    }
}
