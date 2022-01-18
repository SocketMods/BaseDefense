package tk.sciwhiz12.basedefense.item.key;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tk.sciwhiz12.basedefense.api.ITooltipInfo;
import tk.sciwhiz12.basedefense.capabilities.CodedKey;
import tk.sciwhiz12.basedefense.capabilities.SerializableCapabilityProvider;
import tk.sciwhiz12.basedefense.item.IColorable;
import tk.sciwhiz12.basedefense.util.ItemHelper;

import java.util.List;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.*;
import static tk.sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class KeyItem extends Item implements IColorable {
    public KeyItem() {
        super(new Item.Properties().durability(0).tab(ITEM_GROUP));
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tile = world.getBlockEntity(pos);
        return tile != null && tile.getCapability(LOCK).isPresent();
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        stack.getCapability(KEY).filter(ITooltipInfo.class::isInstance)
                .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        if (!flagIn.isAdvanced()) return;
        ItemHelper.addColorInformation(stack, tooltip);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new SerializableCapabilityProvider<>(CodedKey::new, KEY, CONTAINS_CODE, CODE_HOLDER);
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
