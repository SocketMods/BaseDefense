package sciwhiz12.basedefense.item.key;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
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
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import sciwhiz12.basedefense.capabilities.ItemHandlerKey;
import sciwhiz12.basedefense.client.render.KeyringRenderer;
import sciwhiz12.basedefense.container.KeyringContainer;
import sciwhiz12.basedefense.util.ItemHelper;

import java.util.List;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
import static sciwhiz12.basedefense.Reference.Capabilities.KEY;
import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class KeyringItem extends Item {
    public KeyringItem() {
        super(new Item.Properties().tab(ITEM_GROUP).durability(0).setISTER(() -> KeyringRenderer::new));
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        stack.getCapability(ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
            int keys = 0;
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack key = handler.getStackInSlot(i);
                if (!key.isEmpty() && key.getItem() instanceof KeyItem) { keys++; }
            }
            if (keys > 0) {
                tooltip.add(new TranslationTextComponent("tooltip.basedefense.keyring.count", keys)
                        .withStyle(TextFormatting.GRAY));
            }
        });
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        if (world.isAreaLoaded(pos, 0)) {
            TileEntity te = world.getBlockEntity(pos);
            return te != null && te.getCapability(LOCK).isPresent();
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide && playerIn.isShiftKeyDown()) {
            NetworkHooks.openGui((ServerPlayerEntity) playerIn,
                    new SimpleNamedContainerProvider((id, inv, player) -> new KeyringContainer(id, inv, stack),
                            stack.getHoverName()), buf -> buf.writeItem(stack));
            return ActionResult.success(stack);
        }
        return ActionResult.pass(stack);
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
        return new KeyringProvider();
    }

    public static class KeyringProvider implements ICapabilitySerializable<INBT> {
        private final IItemHandler item = createItemHandler();
        private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> item);
        private final LazyOptional<ItemHandlerKey> key = LazyOptional.of(() -> new ItemHandlerKey(item));

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap == ITEM_HANDLER_CAPABILITY) { return itemCap.cast(); }
            if (cap == KEY) { return key.cast(); }
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
        public INBT serializeNBT() {
            return ITEM_HANDLER_CAPABILITY.writeNBT(item, null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            ITEM_HANDLER_CAPABILITY.readNBT(item, null, nbt);
        }
    }
}
