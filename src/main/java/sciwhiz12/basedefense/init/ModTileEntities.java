package sciwhiz12.basedefense.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.tileentity.LockableDoorTile;
import sciwhiz12.basedefense.tileentity.TestLockTile;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> REGISTER = new DeferredRegister<>(
        ForgeRegistries.TILE_ENTITIES, BaseDefense.MODID
    );

    public static final RegistryObject<TileEntityType<LockableDoorTile>> LOCK_DOOR_TILE = ModTileEntities.REGISTER
        .register(
            "lockable_door_tile", () -> TileEntityType.Builder.create(
                LockableDoorTile::new, ModBlocks.LOCK_DOOR_BLOCK.get()
            ).build(null)
        );
    public static final RegistryObject<TileEntityType<TestLockTile>> TEST_LOCK_TILE = ModTileEntities.REGISTER
        .register(
            "test_lock_tile", () -> TileEntityType.Builder.create(
                TestLockTile::new, ModBlocks.TEST_LOCK_BLOCK.get()
            ).build(null)
        );
}
