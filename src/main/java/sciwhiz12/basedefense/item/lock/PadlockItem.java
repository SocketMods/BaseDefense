package sciwhiz12.basedefense.item.lock;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemHandlerHelper;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.capabilities.CodedLock;
import sciwhiz12.basedefense.capabilities.GenericCapabilityProvider;
import sciwhiz12.basedefense.init.ModCapabilities;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.tileentity.LockableTile;

public class PadlockItem extends LockBaseItem implements IColorable {
    private static final IItemPropertyGetter COLOR_GETTER = (stack, world, livingEntity) -> {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) { return (float) tag.getIntArray("colors").length; }
        return 0.0F;
    };

    public PadlockItem() {
        super(new Item.Properties().maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("colors"), COLOR_GETTER);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new GenericCapabilityProvider<>(ModCapabilities.LOCK, () -> new CodedLock() {
            @Override
            public void onRemove(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
                worldPos.consume((world, pos) -> {
                    TileEntity te = world.getTileEntity(pos);
                    if (te != null && te instanceof LockableTile) {
                        LockableTile lockTile = (LockableTile) te;
                        ItemHandlerHelper.giveItemToPlayer(player, lockTile.getLockStack());
                        lockTile.setLockStack(ItemStack.EMPTY);
                    }
                });
            }
        });
    }
}
