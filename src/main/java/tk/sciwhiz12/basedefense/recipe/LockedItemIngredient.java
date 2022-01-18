package tk.sciwhiz12.basedefense.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.Reference.IngredientSerializers;

import java.util.Objects;
import java.util.stream.Stream;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.CODE_HOLDER;
import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;

public class LockedItemIngredient extends Ingredient {
    private final ItemStack stack;
    private final boolean requiresCode;

    protected LockedItemIngredient(ItemStack stack, boolean requiresCode) {
        super(Stream.of(new Ingredient.ItemValue(stack)));
        this.stack = stack;
        this.requiresCode = requiresCode;
    }

    public LockedItemIngredient(ItemLike item, boolean requiresCode) {
        this(new ItemStack(item), requiresCode);
    }

    public boolean requiresCode() {
        return requiresCode;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null) return false;
        final boolean itemAndDamage = this.stack.getItem() == input.getItem() && this.stack.getDamageValue() == input.getDamageValue();
        final boolean hasLock = input.getCapability(LOCK).isPresent();
        final boolean hasCode = (!requiresCode || input.getCapability(CODE_HOLDER)
            .map(holder -> holder.getCodes().size() > 0).orElse(false));
        return itemAndDamage && hasLock && hasCode;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return IngredientSerializers.LOCKED_ITEM;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", Objects.requireNonNull(CraftingHelper.getID(IngredientSerializers.LOCKED_ITEM)).toString());
        json.addProperty("has_codes", requiresCode);
        json.addProperty("item", Objects.requireNonNull(stack.getItem().getRegistryName()).toString());
        return json;
    }

    public static class Serializer implements IIngredientSerializer<LockedItemIngredient> {
        @Override
        public LockedItemIngredient parse(FriendlyByteBuf buffer) {
            return new LockedItemIngredient(buffer.readItem(), buffer.readBoolean());
        }

        @Override
        public LockedItemIngredient parse(JsonObject json) {
            return new LockedItemIngredient(CraftingHelper.getItemStack(json, true),
                GsonHelper.getAsBoolean(json, "has_codes"));
        }

        @Override
        public void write(FriendlyByteBuf buffer, LockedItemIngredient ingredient) {
            buffer.writeItem(ingredient.stack);
            buffer.writeBoolean(ingredient.requiresCode);
        }
    }
}
