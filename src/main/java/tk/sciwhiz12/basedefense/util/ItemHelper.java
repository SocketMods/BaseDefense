package tk.sciwhiz12.basedefense.util;

import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import tk.sciwhiz12.basedefense.api.capablities.ICodeHolder;
import tk.sciwhiz12.basedefense.capabilities.CodedKey;
import tk.sciwhiz12.basedefense.capabilities.CodedLock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;

/**
 * Helper methods for {@link ItemStack}s and codes.
 *
 * @author SciWhiz12
 */
public final class ItemHelper {
    // Prevent instantiation
    private ItemHelper() {}

    public static void addCodeInformation(ItemStack stack, @Nullable List<Component> tooltip) {
        checkNotNull(stack);
        tooltip = tooltip != null ? tooltip : new ArrayList<>();
        List<Long> ids = stack.getCapability(CODE_HOLDER).map(ICodeHolder::getCodes).orElse(Collections.emptyList());
        if (ids.size() != 0) {
            tooltip.add(new TranslatableComponent("tooltip.basedefense.storedcodes").withStyle(ChatFormatting.GRAY));
            for (long id : ids) {
                tooltip.add(new TextComponent("  " + String.format("%016X", id)).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    public static void addColorInformation(ItemStack stack, @Nullable List<Component> tooltip) {
        checkNotNull(stack);
        tooltip = tooltip != null ? tooltip : new ArrayList<>();
        CompoundTag tag = stack.getTagElement("display");
        if (stack.hasTag() && tag != null && tag.contains("colors")) {
            int[] colors = tag.getIntArray("colors");
            for (int i = 0; i < colors.length; i++) {
                tooltip.add(new TranslatableComponent("tooltip.basedefense.color", i + 1,
                        new TextComponent(String.format("#%06X", colors[i])).withStyle(ChatFormatting.DARK_GRAY))
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Nullable
    public static <T> CompoundTag getItemShareTag(ItemStack stack, CapabilitySerializer<?, ?>... caps) {
        checkNotNull(stack);
        checkNotNull(caps);
        CompoundTag shareTag = new CompoundTag();
        if (stack.hasTag()) { shareTag.put("Tag", stack.getTag()); }
        for (CapabilitySerializer<?, ?> cap : caps) {
            accept(stack, cap, (serializer, inst) -> {
                final Tag nbt = serializer.serializer.apply(inst);
                if (nbt != null) { shareTag.put(serializer.capability().getName(), nbt); }
            });
        }
        return !shareTag.isEmpty() ? shareTag : null;
    }

    public static void readItemShareTag(ItemStack stack, @Nullable CompoundTag nbt, CapabilitySerializer<?, ?>... caps) {
        checkNotNull(stack);
        checkNotNull(caps);
        if (nbt == null) { return; }
        if (nbt.contains("Tag", Tag.TAG_COMPOUND)) { stack.setTag(nbt.getCompound("Tag")); }
        for (CapabilitySerializer<?, ?> cap : caps) {
            final Tag tag = nbt.get(cap.capability().getName());
            if (tag != null && cap.tagClass().isAssignableFrom(tag.getClass())) {
                acceptTag(stack, cap, tag);
            }
        }
    }

    private static <C, T extends Tag> void accept(ICapabilityProvider provider, CapabilitySerializer<C, T> cap,
                                                  BiConsumer<CapabilitySerializer<C, T>, C> consumer) {
        provider.getCapability(cap.capability()).ifPresent(inst -> {
            if (cap.capabilityInstanceClass().isInstance(inst)) {
                consumer.accept(cap, (C) inst);
            }
        });
    }

    private static <C, T extends Tag> void acceptTag(ICapabilityProvider provider, CapabilitySerializer<C, T> cap,
                                                     Tag tag) {
        provider.getCapability(cap.capability()).ifPresent(inst -> {
            if (cap.capabilityInstanceClass().isInstance(inst)) {
                cap.deserializer().accept((C) inst, (T) tag);
            }
        });
    }

    public static void copyCodes(ItemStack fromStack, ItemStack toStack) {
        checkNotNull(fromStack);
        checkNotNull(toStack);
        Util.consumeIfPresent(fromStack.getCapability(CODE_HOLDER), toStack.getCapability(CODE_HOLDER),
                (from, to) -> to.setCodes(from.getCodes()));
    }

    public record CapabilitySerializer<C, T extends Tag>(Class<C> capabilityInstanceClass,
                                                         Class<T> tagClass,
                                                         Capability<? super C> capability,
                                                         Function<C, T> serializer,
                                                         BiConsumer<C, T> deserializer) {

        public static final CapabilitySerializer<ItemStackHandler, CompoundTag> ITEM_STACK_HANDLER = new CapabilitySerializer<>(
            ItemStackHandler.class, CompoundTag.class, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
            ItemStackHandler::serializeNBT, ItemStackHandler::deserializeNBT
        );

        public static final CapabilitySerializer<CodedLock, LongArrayTag> CODED_LOCK = new CapabilitySerializer<>(
            CodedLock.class, LongArrayTag.class, CODE_HOLDER,
            CodedLock::serializeNBT, CodedLock::deserializeNBT
        );

        public static final CapabilitySerializer<CodedKey, LongTag> CODED_KEY = new CapabilitySerializer<>(
            CodedKey.class, LongTag.class, CODE_HOLDER,
            CodedKey::serializeNBT, CodedKey::deserializeNBT
        );
    }
}
