package tk.sciwhiz12.basedefense;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.block.LockedDoorBlock;
import tk.sciwhiz12.basedefense.item.IColorable;
import tk.sciwhiz12.basedefense.tileentity.LockedDoorTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static tk.sciwhiz12.basedefense.Reference.modLoc;

/**
 * Holds references to <strong>client-side only</strong> constants and objects
 * created and registered by this mod.
 *
 * @author SciWhiz12
 */
public final class ClientReference {
    public static final class Colors {
        public static final ItemColor ITEM_COLOR = (stack, tintIndex) -> {
            if (stack.getItem() instanceof IColorable color && tintIndex >= 2) {
                return color.getColor(stack, tintIndex - 2);
            }
            return -1;
        };

        public static final BlockColor LOCKED_DOOR_COLOR = (state, world, pos, tintIndex) -> {
            if (world != null && pos != null && state.getBlock() instanceof LockedDoorBlock) {
                if (world.getBlockEntity(pos) instanceof LockedDoorTile tile && tile.hasColors()) {
                    int[] colors = tile.getColors();
                    // offset by 1 since index 0 is reserved for particle color
                    if (tintIndex != 0 && colors.length > tintIndex - 1) {
                        return colors[tintIndex - 1];
                    }
                }
            }
            return -1;
        };

        // Prevent instantiation
        private Colors() {
        }
    }

    public static final class ModelLayers {
        public static final ModelLayerLocation PORTABLE_SAFE =
            new ModelLayerLocation(Objects.requireNonNull(Reference.Blocks.PORTABLE_SAFE.getRegistryName()), "portable_safe");

        // Prevent instantiation
        private ModelLayers() {
        }
    }

    public static final class PropertyOverrides {
        public static final ResourceLocation COLORS = new ResourceLocation("colors");

        public static final ItemPropertyFunction COLORS_GETTER = (stack, world, livingEntity, seed) -> {
            @Nullable CompoundTag tag = stack.getTagElement("display");
            if (tag != null && tag.contains("colors")) {
                return (float) tag.getIntArray("colors").length;
            }
            return 0.0F;
        };

        // Prevent instantiation
        private PropertyOverrides() {
        }
    }

    public static final class Textures {
        static final List<ResourceLocation> SPRITE_LIST = new ArrayList<>();

        public static final ResourceLocation ATLAS_BLOCKS_TEXTURE = InventoryMenu.BLOCK_ATLAS;

        public static final ResourceLocation SLOT_KEY = addSprite("item/slot_key");
        public static final ResourceLocation SLOT_BLANK_KEY = addSprite("item/slot_blank_key");
        public static final ResourceLocation SLOT_LOCK_CORE = addSprite("item/slot_lock_core");
        public static final ResourceLocation SLOT_INGOT_OUTLINE = addSprite("item/slot_ingot_outline");

        public static final ResourceLocation PORTABLE_SAFE_MODEL = modLoc("textures/model/portable_safe.png");

        public static final ResourceLocation KEYRING_GUI = modLoc("textures/gui/keyring_gui.png");
        public static final ResourceLocation KEYSMITH_GUI = modLoc("textures/gui/keysmith_gui.png");
        public static final ResourceLocation LOCKSMITH_GUI = modLoc("textures/gui/locksmith_gui.png");
        public static final ResourceLocation PORTABLE_SAFE_GUI = modLoc("textures/gui/portable_safe_gui.png");

        static ResourceLocation addSprite(String location) {
            ResourceLocation loc = modLoc(location);
            SPRITE_LIST.add(loc);
            return loc;
        }

        // Prevent instantiation
        private Textures() {
        }
    }

    // Prevent instantiation
    private ClientReference() {
    }
}
