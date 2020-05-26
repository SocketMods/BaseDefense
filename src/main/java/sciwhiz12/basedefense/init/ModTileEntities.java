package sciwhiz12.basedefense.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.tileentity.PadlockedDoorTile;
import sciwhiz12.basedefense.tileentity.LockableTile;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> REGISTER = new DeferredRegister<>(
        ForgeRegistries.TILE_ENTITIES, BaseDefense.MODID
    );

    public static final RegistryObject<TileEntityType<LockableTile>> LOCKABLE_TILE = ModTileEntities.REGISTER.register(
        "lockable_tile", () -> TileEntityType.Builder.create(LockableTile::new, ModBlocks.TEST_LOCK_BLOCK.get()).build(null)
    );

    public static final RegistryObject<TileEntityType<PadlockedDoorTile>> PADLOCKED_DOOR = ModTileEntities.REGISTER.register(
        "padlocked_door", () -> TileEntityType.Builder.create(
            PadlockedDoorTile::new, ModBlocks.PADLOCKED_IRON_DOOR.get(), ModBlocks.PADLOCKED_OAK_DOOR.get(),
            ModBlocks.PADLOCKED_BIRCH_DOOR.get(), ModBlocks.PADLOCKED_SPRUCE_DOOR.get(), ModBlocks.PADLOCKED_JUNGLE_DOOR
                .get(), ModBlocks.PADLOCKED_ACACIA_DOOR.get(), ModBlocks.PADLOCKED_DARK_OAK_DOOR.get()
        ).build(null)
    );
}
