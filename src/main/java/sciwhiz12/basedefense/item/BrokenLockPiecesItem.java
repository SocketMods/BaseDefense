package sciwhiz12.basedefense.item;

import static sciwhiz12.basedefense.Reference.ITEM_GROUP;
import static sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static sciwhiz12.basedefense.Reference.Capabilities.CONTAINS_CODE;

import java.util.List;

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
import sciwhiz12.basedefense.capabilities.CodedLock;
import sciwhiz12.basedefense.capabilities.SerializableCapabilityProvider;
import sciwhiz12.basedefense.util.ItemHelper;

public class BrokenLockPiecesItem extends Item implements IColorable {
    public BrokenLockPiecesItem() {
        super(new Item.Properties().maxDamage(0).group(ITEM_GROUP));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (hasPreviousName(stack)) { tooltip.add(getPreviousName(stack).func_240699_a_(TextFormatting.ITALIC)); }
        if (!flagIn.isAdvanced()) return;
        ItemHelper.addCodeInformation(stack, tooltip);
        ItemHelper.addColorInformation(stack, tooltip);
    }

    public boolean hasPreviousName(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("BrokenLockName", Constants.NBT.TAG_STRING);
    }

    public IFormattableTextComponent getPreviousName(ItemStack stack) {
        return hasPreviousName(stack) ? ITextComponent.Serializer.func_240643_a_(stack.getTag().getString("BrokenLockName"))
                : new StringTextComponent("");
    }

    public void setPreviousName(ItemStack stack, ITextComponent name) {
        stack.getOrCreateTag().putString("BrokenLockName", ITextComponent.Serializer.toJson(name));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        // Using CodedLock for code holder capability, not for lock capability
        return new SerializableCapabilityProvider<>(CodedLock::new, CONTAINS_CODE, CODE_HOLDER);
    }

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, CODE_HOLDER);
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        ItemHelper.readItemShareTag(stack, nbt, CODE_HOLDER);
    }
}
