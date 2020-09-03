package sciwhiz12.basedefense;

import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.tileentity.LockedDoorTile;

import java.util.ArrayList;
import java.util.List;

import static sciwhiz12.basedefense.Reference.modLoc;

/**
 * Holds references to <strong>client-side only</strong> constants and objects
 * created and registered by this mod.
 *
 * @author SciWhiz12
 */
public final class ClientReference {
    public static final class Colors {
        public static final IItemColor ITEM_COLOR = (stack, tintIndex) -> {
            if (stack.getItem() instanceof IColorable && tintIndex >= 2) {
                return ((IColorable) stack.getItem()).getColor(stack, tintIndex - 2);
            }
            return -1;
        };

        public static final IBlockColor LOCKED_DOOR_COLOR = (state, world, pos, tintIndex) -> {
            if (world != null && pos != null && state.getBlock() instanceof LockedDoorBlock) {
                TileEntity tile = world.getTileEntity(pos);
                if (tile instanceof LockedDoorTile && ((LockedDoorTile) tile).hasColors()) {
                    int[] colors = ((LockedDoorTile) tile).getColors();
                    // offset by 1 since index 0 is reserved for particle color
                    if (tintIndex != 0 && colors.length > tintIndex - 1) { return colors[tintIndex - 1]; }
                }
            }
            return -1;
        };

        // Prevent instantiation
        private Colors() {}
    }

    public static final class PropertyOverrides {
        public static final ResourceLocation COLORS = new ResourceLocation("colors");

        public static final IItemPropertyGetter COLORS_GETTER = (stack, world, livingEntity) -> {
            CompoundNBT tag = stack.getChildTag("display");
            if (tag != null && tag.contains("colors")) { return (float) tag.getIntArray("colors").length; }
            return 0.0F;
        };

        // Prevent instantiation
        private PropertyOverrides() {}
    }

    public static final class Textures {
        static final List<ResourceLocation> SPRITE_LIST = new ArrayList<>();

        public static final ResourceLocation ATLAS_BLOCKS_TEXTURE = PlayerContainer.LOCATION_BLOCKS_TEXTURE;

        public static final ResourceLocation SLOT_KEY = addSprite("item/slot_key");
        public static final ResourceLocation SLOT_BLANK_KEY = addSprite("item/slot_blank_key");
        public static final ResourceLocation SLOT_LOCK_CORE = addSprite("item/slot_lock_core");
        public static final ResourceLocation SLOT_INGOT_OUTLINE = addSprite("item/slot_ingot_outline");

        public static final ResourceLocation KEYRING_GUI = modLoc("textures/gui/keyring_gui.png");
        public static final ResourceLocation KEYSMITH_GUI = modLoc("textures/gui/keysmith_gui.png");
        public static final ResourceLocation LOCKSMITH_GUI = modLoc("textures/gui/locksmith_gui.png");

        static ResourceLocation addSprite(String location) {
            ResourceLocation loc = modLoc(location);
            SPRITE_LIST.add(loc);
            return loc;
        }

        // Prevent instantiation
        private Textures() {}
    }

    // Prevent instantiation
    private ClientReference() {}
}
