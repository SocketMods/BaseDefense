package sciwhiz12.basedefense.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.tileentity.LockableTile;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> REGISTER = new DeferredRegister<>(
        ForgeRegistries.TILE_ENTITIES, BaseDefense.MODID
    );

    public static final RegistryObject<TileEntityType<LockableTile>> LOCKABLE_TILE = ModTileEntities.REGISTER.register(
        "lockable_tile", () -> TileEntityType.Builder.create(
            LockableTile::new, ModBlocks.PADLOCKED_DOOR.get(), ModBlocks.TEST_LOCK_BLOCK.get()
        ).build(null)
    );
}
