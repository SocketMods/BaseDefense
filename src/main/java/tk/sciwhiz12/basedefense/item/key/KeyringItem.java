package tk.sciwhiz12.basedefense.item.key;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import tk.sciwhiz12.basedefense.capabilities.ItemHandlerKey;
import tk.sciwhiz12.basedefense.client.render.KeyringRenderer;
import tk.sciwhiz12.basedefense.container.KeyringContainer;
import tk.sciwhiz12.basedefense.util.ItemHelper;

import java.util.List;
import java.util.function.Consumer;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.KEY;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static tk.sciwhiz12.basedefense.Reference.ITEM_GROUP;

public class KeyringItem extends Item {
    public KeyringItem() {
        super(new Item.Properties().tab(ITEM_GROUP).durability(0));
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new KeyringRenderer();
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        stack.getCapability(ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
            int keys = 0;
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack key = handler.getStackInSlot(i);
                if (!key.isEmpty() && key.getItem() instanceof KeyItem) { keys++; }
            }
            if (keys > 0) {
                tooltip.add(new TranslatableComponent("tooltip.basedefense.keyring.count", keys)
                        .withStyle(ChatFormatting.GRAY));
            }
        });
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        if (world.isAreaLoaded(pos, 0)) {
            BlockEntity te = world.getBlockEntity(pos);
            return te != null && te.getCapability(LOCK).isPresent();
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide && playerIn.isShiftKeyDown()) {
            NetworkHooks.openGui((ServerPlayer) playerIn,
                    new SimpleMenuProvider((id, inv, player) -> new KeyringContainer(id, inv, stack),
                            stack.getHoverName()), buf -> buf.writeItem(stack));
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, ItemHelper.CapabilitySerializer.ITEM_STACK_HANDLER);
    }

    @Override

    public void readShareTag(ItemStack stack, CompoundTag nbt) {
        ItemHelper.readItemShareTag(stack, nbt, ItemHelper.CapabilitySerializer.ITEM_STACK_HANDLER);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new KeyringProvider();
    }

    public static class KeyringProvider implements ICapabilitySerializable<CompoundTag> {
        private final ItemStackHandler item = createItemHandler();
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
        public CompoundTag serializeNBT() {
            return item.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            item.deserializeNBT(nbt);
        }
    }
}
