package sciwhiz12.basedefense.item.key;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import sciwhiz12.basedefense.capabilities.CodedKey;
import sciwhiz12.basedefense.capabilities.SerializableCapabilityProvider;
import sciwhiz12.basedefense.init.ModCapabilities;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.util.ItemHelper;

public class KeyItem extends Item implements IColorable {
    private static final IItemPropertyGetter COLOR_GETTER = (stack, world, livingEntity) -> {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) { return (float) tag.getIntArray("colors").length; }
        return 0.0F;
    };

    public KeyItem() {
        super(new Item.Properties().maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("colors"), COLOR_GETTER);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return world.isBlockLoaded(pos) && world.getTileEntity(pos) != null && world.getTileEntity(pos).getCapability(
            ModCapabilities.LOCK).isPresent();
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!flagIn.isAdvanced()) return;
        ItemHelper.addCodeInformation(stack, tooltip);
        ItemHelper.addColorInformation(stack, tooltip);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new SerializableCapabilityProvider<>(CodedKey::new, ModCapabilities.KEY, ModCapabilities.CONTAINS_CODE,
            ModCapabilities.CODE_HOLDER);
    }

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, ModCapabilities.CODE_HOLDER);
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        ItemHelper.readItemShareTag(stack, nbt, ModCapabilities.CODE_HOLDER);
    }
}
