package sciwhiz12.basedefense.item.lock;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.capabilities.CodedLock;
import sciwhiz12.basedefense.capabilities.GenericCapabilityProvider;
import sciwhiz12.basedefense.init.ModCapabilities;
import sciwhiz12.basedefense.item.IColorable;

public class LockCoreItem extends LockBaseItem implements IColorable {
    private static final IItemPropertyGetter COLOR_GETTER = (stack, world, livingEntity) -> {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) { return (float) tag.getIntArray("colors").length; }
        return 0.0F;
    };

    public LockCoreItem() {
        super(new Item.Properties().maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("colors"), COLOR_GETTER);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return world.getBlockState(pos).getBlock() instanceof LockedDoorBlock;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new GenericCapabilityProvider<>(ModCapabilities.LOCK, CodedLock::new);
    }
}
