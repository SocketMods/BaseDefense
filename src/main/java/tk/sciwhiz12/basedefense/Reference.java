package tk.sciwhiz12.basedefense;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ObjectHolder;
import tk.sciwhiz12.basedefense.api.capablities.ICodeHolder;
import tk.sciwhiz12.basedefense.api.capablities.IContainsCode;
import tk.sciwhiz12.basedefense.api.capablities.IKey;
import tk.sciwhiz12.basedefense.api.capablities.ILock;
import tk.sciwhiz12.basedefense.block.KeysmithBlock;
import tk.sciwhiz12.basedefense.block.LockedDoorBlock;
import tk.sciwhiz12.basedefense.block.LocksmithBlock;
import tk.sciwhiz12.basedefense.block.PadlockedDoorBlock;
import tk.sciwhiz12.basedefense.block.PortableSafeBlock;
import tk.sciwhiz12.basedefense.container.KeyringContainer;
import tk.sciwhiz12.basedefense.container.KeysmithContainer;
import tk.sciwhiz12.basedefense.container.LocksmithContainer;
import tk.sciwhiz12.basedefense.container.PortableSafeContainer;
import tk.sciwhiz12.basedefense.item.BrokenLockPiecesItem;
import tk.sciwhiz12.basedefense.item.LockedBlockItem;
import tk.sciwhiz12.basedefense.item.key.AdminKeyItem;
import tk.sciwhiz12.basedefense.item.key.KeyItem;
import tk.sciwhiz12.basedefense.item.key.KeyringItem;
import tk.sciwhiz12.basedefense.item.lock.AdminLockCoreItem;
import tk.sciwhiz12.basedefense.item.lock.AdminPadlockItem;
import tk.sciwhiz12.basedefense.item.lock.CodedLockCoreItem;
import tk.sciwhiz12.basedefense.item.lock.CodedPadlockItem;
import tk.sciwhiz12.basedefense.recipe.CodedLockRecipe;
import tk.sciwhiz12.basedefense.recipe.ColoringRecipe;
import tk.sciwhiz12.basedefense.recipe.LockedItemIngredient;
import tk.sciwhiz12.basedefense.recipe.LockedItemRecipe;
import tk.sciwhiz12.basedefense.tileentity.LockableTile;
import tk.sciwhiz12.basedefense.tileentity.LockedDoorTile;
import tk.sciwhiz12.basedefense.tileentity.PadlockedDoorTile;
import tk.sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;
import tk.sciwhiz12.basedefense.util.RecipeHelper;
import tk.sciwhiz12.basedefense.util.Util;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Holds references to constants and objects created and registered by this mod.
 *
 * @author SciWhiz12
 */
public final class Reference {
    public static final String MODID = "basedefense";

    @ObjectHolder(MODID)
    public static final class Blocks {
        public static final KeysmithBlock KEYSMITH_TABLE = Util.Null();
        public static final LocksmithBlock LOCKSMITH_TABLE = Util.Null();

        public static final PadlockedDoorBlock PADLOCKED_IRON_DOOR = Util.Null();
        public static final PadlockedDoorBlock PADLOCKED_OAK_DOOR = Util.Null();
        public static final PadlockedDoorBlock PADLOCKED_BIRCH_DOOR = Util.Null();
        public static final PadlockedDoorBlock PADLOCKED_SPRUCE_DOOR = Util.Null();
        public static final PadlockedDoorBlock PADLOCKED_JUNGLE_DOOR = Util.Null();
        public static final PadlockedDoorBlock PADLOCKED_ACACIA_DOOR = Util.Null();
        public static final PadlockedDoorBlock PADLOCKED_DARK_OAK_DOOR = Util.Null();
        public static final PadlockedDoorBlock PADLOCKED_CRIMSON_DOOR = Util.Null();
        public static final PadlockedDoorBlock PADLOCKED_WARPED_DOOR = Util.Null();

        public static final LockedDoorBlock LOCKED_IRON_DOOR = Util.Null();
        public static final LockedDoorBlock LOCKED_OAK_DOOR = Util.Null();
        public static final LockedDoorBlock LOCKED_BIRCH_DOOR = Util.Null();
        public static final LockedDoorBlock LOCKED_SPRUCE_DOOR = Util.Null();
        public static final LockedDoorBlock LOCKED_JUNGLE_DOOR = Util.Null();
        public static final LockedDoorBlock LOCKED_ACACIA_DOOR = Util.Null();
        public static final LockedDoorBlock LOCKED_DARK_OAK_DOOR = Util.Null();
        public static final LockedDoorBlock LOCKED_CRIMSON_DOOR = Util.Null();
        public static final LockedDoorBlock LOCKED_WARPED_DOOR = Util.Null();

        public static final PortableSafeBlock PORTABLE_SAFE = Util.Null();

        // Prevent instantiation
        private Blocks() {}
    }

    public static final class Capabilities {
        public static final Capability<ILock> LOCK = CapabilityManager.get(new CapabilityToken<>() {
        });
        public static final Capability<IKey> KEY = CapabilityManager.get(new CapabilityToken<>() {
        });
        public static final Capability<IContainsCode> CONTAINS_CODE = CapabilityManager.get(new CapabilityToken<>() {
        });
        public static final Capability<ICodeHolder> CODE_HOLDER = CapabilityManager.get(new CapabilityToken<>() {
        });

        // Prevent instantiation
        private Capabilities() {}
    }

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.LOCK_CORE);
        }
    };

    @ObjectHolder(MODID)
    public static final class Items {
        public static final Item BLANK_KEY = Util.Null();
        public static final KeyItem KEY = Util.Null();
        public static final CodedLockCoreItem LOCK_CORE = Util.Null();
        public static final CodedPadlockItem PADLOCK = Util.Null();
        public static final KeyringItem KEYRING = Util.Null();
        public static final BrokenLockPiecesItem BROKEN_LOCK_PIECES = Util.Null();
        public static final AdminKeyItem ADMIN_KEY = Util.Null();
        public static final AdminLockCoreItem ADMIN_LOCK_CORE = Util.Null();
        public static final AdminPadlockItem ADMIN_PADLOCK = Util.Null();

        public static final BlockItem KEYSMITH_TABLE = Util.Null();
        public static final BlockItem LOCKSMITH_TABLE = Util.Null();

        public static final LockedBlockItem LOCKED_IRON_DOOR = Util.Null();
        public static final LockedBlockItem LOCKED_OAK_DOOR = Util.Null();
        public static final LockedBlockItem LOCKED_BIRCH_DOOR = Util.Null();
        public static final LockedBlockItem LOCKED_SPRUCE_DOOR = Util.Null();
        public static final LockedBlockItem LOCKED_JUNGLE_DOOR = Util.Null();
        public static final LockedBlockItem LOCKED_ACACIA_DOOR = Util.Null();
        public static final LockedBlockItem LOCKED_CRIMSON_DOOR = Util.Null();
        public static final LockedBlockItem LOCKED_WARPED_DOOR = Util.Null();
        public static final LockedBlockItem LOCKED_DARK_OAK_DOOR = Util.Null();

        public static final LockedBlockItem PORTABLE_SAFE = Util.Null();

        // Prevent instantiation
        private Items() {}
    }

    @ObjectHolder(MODID)
    public static final class Containers {
        public static final MenuType<KeysmithContainer> KEYSMITH_TABLE = Util.Null();
        public static final MenuType<LocksmithContainer> LOCKSMITH_TABLE = Util.Null();
        public static final MenuType<KeyringContainer> KEYRING = Util.Null();
        public static final MenuType<PortableSafeContainer> PORTABLE_SAFE = Util.Null();

        // Prevent instantiation
        private Containers() {}
    }

    @ObjectHolder(MODID)
    public static final class RecipeSerializers {
        public static final RecipeHelper.ShapedSerializer<LockedItemRecipe> LOCKED_ITEM = Util.Null();
        public static final RecipeHelper.ShapedSerializer<CodedLockRecipe> CODED_LOCK = Util.Null();
        public static final SimpleRecipeSerializer<ColoringRecipe> COLORING = Util.Null();

        // Prevent instantiation
        private RecipeSerializers() {}
    }

    public static final class IngredientSerializers {
        public static IIngredientSerializer<LockedItemIngredient> LOCKED_ITEM = Util.Null();

        // Prevent instantiation
        private IngredientSerializers() {}
    }

    @ObjectHolder(MODID)
    public static final class Sounds {
        public static final SoundEvent LOCKED_DOOR_ATTEMPT = Util.Null();
        public static final SoundEvent LOCKED_DOOR_RELOCK = Util.Null();
        public static final SoundEvent LOCKED_DOOR_UNLOCK = Util.Null();

        // Prevent instantiation
        private Sounds() {}
    }

    @ObjectHolder(MODID)
    public static final class TileEntities {
        public static final BlockEntityType<LockableTile> LOCKABLE_TILE = Util.Null();
        public static final BlockEntityType<PadlockedDoorTile> PADLOCKED_DOOR = Util.Null();
        public static final BlockEntityType<LockedDoorTile> LOCKED_DOOR = Util.Null();
        public static final BlockEntityType<PortableSafeTileEntity> PORTABLE_SAFE = Util.Null();

        // Prevent instantiation
        private TileEntities() {}
    }

    // Prevent instantiation
    private Reference() {}

    /**
     * Creates a {@link ResourceLocation} with the namespace as
     * {@link Reference#MODID} and the specified path.
     *
     * @param path The specified path
     * @return A {@code ResourceLocation} with {@link Reference#MODID} and path
     */
    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(MODID, checkNotNull(path));
    }
}
