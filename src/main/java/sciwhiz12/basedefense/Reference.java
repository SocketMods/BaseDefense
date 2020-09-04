package sciwhiz12.basedefense;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ObjectHolder;
import sciwhiz12.basedefense.api.capablities.ICodeHolder;
import sciwhiz12.basedefense.api.capablities.IContainsCode;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;
import sciwhiz12.basedefense.block.KeysmithBlock;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.block.LocksmithBlock;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.block.PortableSafeBlock;
import sciwhiz12.basedefense.container.KeyringContainer;
import sciwhiz12.basedefense.container.KeysmithContainer;
import sciwhiz12.basedefense.container.LocksmithContainer;
import sciwhiz12.basedefense.container.PortableSafeContainer;
import sciwhiz12.basedefense.item.BrokenLockPiecesItem;
import sciwhiz12.basedefense.item.LockedBlockItem;
import sciwhiz12.basedefense.item.key.AdminKeyItem;
import sciwhiz12.basedefense.item.key.KeyItem;
import sciwhiz12.basedefense.item.key.KeyringItem;
import sciwhiz12.basedefense.item.lock.AdminLockCoreItem;
import sciwhiz12.basedefense.item.lock.AdminPadlockItem;
import sciwhiz12.basedefense.item.lock.CodedLockCoreItem;
import sciwhiz12.basedefense.item.lock.CodedPadlockItem;
import sciwhiz12.basedefense.recipe.*;
import sciwhiz12.basedefense.tileentity.LockableTile;
import sciwhiz12.basedefense.tileentity.LockedDoorTile;
import sciwhiz12.basedefense.tileentity.PadlockedDoorTile;
import sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;
import sciwhiz12.basedefense.util.RecipeHelper;

import static com.google.common.base.Preconditions.checkNotNull;
import static sciwhiz12.basedefense.util.Util.Null;

/**
 * Holds references to constants and objects created and registered by this mod.
 *
 * @author SciWhiz12
 */
public final class Reference {
    public static final String MODID = "basedefense";

    @ObjectHolder(MODID)
    public static final class Blocks {
        public static final KeysmithBlock KEYSMITH_TABLE = Null();
        public static final LocksmithBlock LOCKSMITH_TABLE = Null();

        public static final PadlockedDoorBlock PADLOCKED_IRON_DOOR = Null();
        public static final PadlockedDoorBlock PADLOCKED_OAK_DOOR = Null();
        public static final PadlockedDoorBlock PADLOCKED_BIRCH_DOOR = Null();
        public static final PadlockedDoorBlock PADLOCKED_SPRUCE_DOOR = Null();
        public static final PadlockedDoorBlock PADLOCKED_JUNGLE_DOOR = Null();
        public static final PadlockedDoorBlock PADLOCKED_ACACIA_DOOR = Null();
        public static final PadlockedDoorBlock PADLOCKED_DARK_OAK_DOOR = Null();

        public static final LockedDoorBlock LOCKED_IRON_DOOR = Null();
        public static final LockedDoorBlock LOCKED_OAK_DOOR = Null();
        public static final LockedDoorBlock LOCKED_BIRCH_DOOR = Null();
        public static final LockedDoorBlock LOCKED_SPRUCE_DOOR = Null();
        public static final LockedDoorBlock LOCKED_JUNGLE_DOOR = Null();
        public static final LockedDoorBlock LOCKED_ACACIA_DOOR = Null();
        public static final LockedDoorBlock LOCKED_DARK_OAK_DOOR = Null();

        public static final PortableSafeBlock PORTABLE_SAFE = Null();

        // Prevent instantiation
        private Blocks() {}
    }

    public static final class Capabilities {
        @CapabilityInject(ILock.class)
        public static final Capability<ILock> LOCK = Null();
        @CapabilityInject(IKey.class)
        public static final Capability<IKey> KEY = Null();
        @CapabilityInject(IContainsCode.class)
        public static final Capability<IContainsCode> CONTAINS_CODE = Null();
        @CapabilityInject(ICodeHolder.class)
        public static final Capability<ICodeHolder> CODE_HOLDER = Null();

        // Prevent instantiation
        private Capabilities() {}
    }

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.LOCK_CORE);
        }
    };

    @ObjectHolder(MODID)
    public static final class Items {
        public static final Item BLANK_KEY = Null();
        public static final KeyItem KEY = Null();
        public static final CodedLockCoreItem LOCK_CORE = Null();
        public static final CodedPadlockItem PADLOCK = Null();
        public static final KeyringItem KEYRING = Null();
        public static final BrokenLockPiecesItem BROKEN_LOCK_PIECES = Null();
        public static final AdminKeyItem ADMIN_KEY = Null();
        public static final AdminLockCoreItem ADMIN_LOCK_CORE = Null();
        public static final AdminPadlockItem ADMIN_PADLOCK = Null();

        public static final BlockItem KEYSMITH_TABLE = Null();
        public static final BlockItem LOCKSMITH_TABLE = Null();

        public static final LockedBlockItem LOCKED_IRON_DOOR = Null();
        public static final LockedBlockItem LOCKED_OAK_DOOR = Null();
        public static final LockedBlockItem LOCKED_BIRCH_DOOR = Null();
        public static final LockedBlockItem LOCKED_SPRUCE_DOOR = Null();
        public static final LockedBlockItem LOCKED_JUNGLE_DOOR = Null();
        public static final LockedBlockItem LOCKED_ACACIA_DOOR = Null();
        public static final LockedBlockItem LOCKED_DARK_OAK_DOOR = Null();

        public static final LockedBlockItem PORTABLE_SAFE = Null();

        // Prevent instantiation
        private Items() {}
    }

    @ObjectHolder(MODID)
    public static final class Containers {
        public static final ContainerType<KeysmithContainer> KEYSMITH_TABLE = Null();
        public static final ContainerType<LocksmithContainer> LOCKSMITH_TABLE = Null();
        public static final ContainerType<KeyringContainer> KEYRING = Null();
        public static final ContainerType<PortableSafeContainer> PORTABLE_SAFE = Null();

        // Prevent instantiation
        private Containers() {}
    }

    @ObjectHolder(MODID)
    public static final class RecipeSerializers {
        public static final RecipeHelper.ShapedSerializer<LockedItemRecipe> LOCKED_ITEM = Null();
        public static final RecipeHelper.ShapedSerializer<CodedLockRecipe> CODED_LOCK = Null();
        public static final SpecialRecipeSerializer<ColoringRecipe> COLORING = Null();

        // Prevent instantiation
        private RecipeSerializers() {}
    }

    public static final class IngredientSerializers {
        public static IIngredientSerializer<LockedItemIngredient> LOCKED_ITEM = Null();

        // Prevent instantiation
        private IngredientSerializers() {}
    }

    @ObjectHolder(MODID)
    public static final class Sounds {
        public static final SoundEvent LOCKED_DOOR_ATTEMPT = Null();
        public static final SoundEvent LOCKED_DOOR_RELOCK = Null();
        public static final SoundEvent LOCKED_DOOR_UNLOCK = Null();

        // Prevent instantiation
        private Sounds() {}
    }

    @ObjectHolder(MODID)
    public static final class TileEntities {
        public static final TileEntityType<LockableTile> LOCKABLE_TILE = Null();
        public static final TileEntityType<PadlockedDoorTile> PADLOCKED_DOOR = Null();
        public static final TileEntityType<LockedDoorTile> LOCKED_DOOR = Null();
        public static final TileEntityType<PortableSafeTileEntity> PORTABLE_SAFE = Null();

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
