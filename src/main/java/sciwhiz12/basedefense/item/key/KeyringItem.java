package sciwhiz12.basedefense.item.key;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import sciwhiz12.basedefense.capabilities.ItemHandlerKey;
import sciwhiz12.basedefense.client.render.KeyringRenderer;
import sciwhiz12.basedefense.container.KeyringContainer.Provider;
import sciwhiz12.basedefense.init.ModCapabilities;
import sciwhiz12.basedefense.init.ModItems;

public class KeyringItem extends Item {
    public KeyringItem() {
        super(new Item.Properties().group(ModItems.GROUP).maxDamage(0).setISTER(() -> KeyringRenderer::new));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
            int keys = 0;
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack key = handler.getStackInSlot(i);
                if (!key.isEmpty() && key.getItem() instanceof KeyItem) { keys++; }
            }
            if (keys > 0) {
                tooltip.add(new TranslationTextComponent("tooltip.basedefense.keyring.count", keys).applyTextStyle(
                    TextFormatting.GRAY));
            }
        });
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        if (world.isBlockLoaded(pos)) {
            TileEntity te = world.getTileEntity(pos);
            return te != null && te.getCapability(ModCapabilities.LOCK).isPresent();
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            playerIn.openContainer(new Provider(stack));
            return ActionResult.resultSuccess(stack);
        }
        return ActionResult.resultPass(stack);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new KeyringProvider();
    }

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT nbt = new CompoundNBT();
        if (stack.hasTag()) { nbt.put("Tag", stack.getTag()); }
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
            nbt.put("ItemHandlerCap", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(handler, null));
        });
        return nbt;
    }

    @Override

    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        if (nbt == null) return;
        if (nbt.contains("Tag")) { stack.setTag(nbt.getCompound("Tag")); }
        if (nbt.contains("ItemHandlerCap")) {
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(handler, null, nbt.get("ItemHandlerCap"));
            });
        }
    }

    public static class KeyringProvider implements ICapabilitySerializable<CompoundNBT> {
        private LazyOptional<IItemHandler> item = LazyOptional.of(this::createItemHandler);
        private LazyOptional<ItemHandlerKey> key = LazyOptional.of(() -> new ItemHandlerKey(item));

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return item.cast(); }
            if (cap == ModCapabilities.KEY) { return key.cast(); }
            return LazyOptional.empty();
        }

        private ItemStackHandler createItemHandler() {
            return new ItemStackHandler(9) {
                @Override
                public boolean isItemValid(int slot, ItemStack stack) {
                    return stack.getItem() instanceof KeyItem;
                }
            };
        }

        @Override
        public CompoundNBT serializeNBT() {
            final CompoundNBT nbt = new CompoundNBT();
            item.ifPresent((handler) -> {
                nbt.put("Items", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(handler, null));
            });
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            item.ifPresent((handler) -> CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(handler, null, nbt.get(
                "Items")));
        }
    }
}
