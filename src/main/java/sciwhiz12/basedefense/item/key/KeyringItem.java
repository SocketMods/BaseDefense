package sciwhiz12.basedefense.item.key;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import sciwhiz12.basedefense.api.lock.IKey;
import sciwhiz12.basedefense.api.lock.ILockable;
import sciwhiz12.basedefense.client.render.KeyringRenderer;
import sciwhiz12.basedefense.container.KeyringContainer.Provider;
import sciwhiz12.basedefense.init.ModItems;

public class KeyringItem extends Item implements IKey {
    public KeyringItem() {
        super(new Item.Properties().group(ModItems.GROUP).maxDamage(0).setISTER(() -> KeyringRenderer::new));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
            int keys = 0;
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack key = handler.getStackInSlot(i);
                if (!key.isEmpty() && key.getItem() instanceof KeyItem) { keys++; }
            }
            if (keys > 0) {
                tooltip.add(
                    new TranslationTextComponent("tooltip.basedefense.keyring.count", keys).applyTextStyle(
                        TextFormatting.GRAY
                    )
                );
            }
        });
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return world.getBlockState(pos).getBlock() instanceof ILockable;
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
    public boolean canUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos, ILockable block,
            PlayerEntity player) {
        LazyOptional<IItemHandler> handler = keyStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        AtomicBoolean ret = new AtomicBoolean(false);
        handler.ifPresent((items) -> {
            for (int i = 0; i < items.getSlots(); i++) {
                ItemStack stack = items.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof IKey) {
                    IKey key = (IKey) stack.getItem();
                    if (key.canUnlock(lockStack, stack, worldIn, pos, block, player)) {
                        ret.set(true);
                        break;
                    }
                }
            }
        });
        return ret.get();
    }

    @Override
    public void onUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos, ILockable block,
            PlayerEntity player) {}

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ItemProvider();
    }

    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT nbt = new CompoundNBT();
        if (stack.hasTag()) { nbt.put("Tag", stack.getTag()); }
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
            (handler) -> {
                nbt.put("ItemHandlerCap", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(handler, null));
            }
        );
        return nbt;
    }

    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        if (nbt.contains("Tag")) { stack.setTag(nbt.getCompound("Tag")); }
        if (nbt.contains("ItemHandlerCap")) {
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
                (handler) -> {
                    CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(handler, null, nbt.get("ItemHandlerCap"));
                }
            );
        }
    }

    public static class ItemProvider implements ICapabilitySerializable<CompoundNBT> {
        private LazyOptional<IItemHandlerModifiable> handler = LazyOptional.of(this::createItemHandler);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return handler.cast(); }
            return LazyOptional.empty();
        }

        private IItemHandlerModifiable createItemHandler() {
            return new ItemStackHandler(9) {
                @Override
                public boolean isItemValid(int slot, ItemStack stack) {
                    return stack.getItem() instanceof KeyItem;
                }
            };
        }

        @Override
        public CompoundNBT serializeNBT() {
            AtomicReference<INBT> ref = new AtomicReference<>();
            handler.ifPresent(
                (handler) -> { ref.set(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(handler, null)); }
            );
            CompoundNBT nbt = new CompoundNBT();
            nbt.put("Items", ref.get());
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            handler.ifPresent(
                (handler) -> CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(
                    handler, null, nbt.getList("Items", Constants.NBT.TAG_COMPOUND)
                )
            );
        }
    }
}
