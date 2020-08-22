package sciwhiz12.basedefense.item.key;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;
import sciwhiz12.basedefense.capabilities.KeyringProvider;
import sciwhiz12.basedefense.client.render.KeyringRenderer;
import sciwhiz12.basedefense.compat.Compatibility;
import sciwhiz12.basedefense.compat.curios.KeyringCuriosProvider;
import sciwhiz12.basedefense.container.KeyringContainer;
import sciwhiz12.basedefense.util.ItemHelper;

import java.util.List;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
import static sciwhiz12.basedefense.Reference.CURIOS_MODID;
import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class KeyringItem extends Item {
    public KeyringItem() {
        super(new Item.Properties().group(ITEM_GROUP).maxDamage(0).setISTER(() -> KeyringRenderer::new));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        stack.getCapability(ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
            int keys = 0;
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack key = handler.getStackInSlot(i);
                if (!key.isEmpty() && key.getItem() instanceof KeyItem) { keys++; }
            }
            if (keys > 0) {
                tooltip.add(new TranslationTextComponent("tooltip.basedefense.keyring.count", keys)
                        .mergeStyle(TextFormatting.GRAY));
            }
        });
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        if (world.isAreaLoaded(pos, 0)) {
            TileEntity te = world.getTileEntity(pos);
            return te != null && te.getCapability(LOCK).isPresent();
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && playerIn.isSneaking()) {
            NetworkHooks.openGui((ServerPlayerEntity) playerIn,
                    new SimpleNamedContainerProvider((id, inv, player) -> new KeyringContainer(id, inv, stack),
                            stack.getDisplayName()), buf -> buf.writeItemStack(stack));
            return ActionResult.resultSuccess(stack);
        }
        return ActionResult.resultPass(stack);
    }

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, ITEM_HANDLER_CAPABILITY);
    }

    @Override

    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        ItemHelper.readItemShareTag(stack, nbt, ITEM_HANDLER_CAPABILITY);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        if (Compatibility.isLoaded(CURIOS_MODID)) {
            return new KeyringCuriosProvider();
        }
        return new KeyringProvider();
    }
}
