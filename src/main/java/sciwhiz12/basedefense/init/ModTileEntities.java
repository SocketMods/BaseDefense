package sciwhiz12.basedefense.init;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.tileentity.LockableTile;
import sciwhiz12.basedefense.tileentity.LockedDoorTile;
import sciwhiz12.basedefense.tileentity.PadlockedDoorTile;

@ObjectHolder(BaseDefense.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModTileEntities {

    public static final TileEntityType<LockableTile> LOCKABLE_TILE = null;
    public static final TileEntityType<PadlockedDoorTile> PADLOCKED_DOOR = null;
    public static final TileEntityType<LockedDoorTile> LOCKED_DOOR = null;

    @SubscribeEvent
    public static void onRegister(RegistryEvent.Register<TileEntityType<?>> event) {
        BaseDefense.LOG.debug("Registering tile entities");
        final IForgeRegistry<TileEntityType<?>> reg = event.getRegistry();

        reg.register(makeType(LockableTile::new, ModBlocks.TEST_LOCK_BLOCK).setRegistryName("lockable_tile"));
        reg.register(
            makeType(
                PadlockedDoorTile::new, ModBlocks.PADLOCKED_IRON_DOOR, ModBlocks.PADLOCKED_OAK_DOOR,
                ModBlocks.PADLOCKED_BIRCH_DOOR, ModBlocks.PADLOCKED_SPRUCE_DOOR, ModBlocks.PADLOCKED_JUNGLE_DOOR,
                ModBlocks.PADLOCKED_ACACIA_DOOR, ModBlocks.PADLOCKED_DARK_OAK_DOOR
            ).setRegistryName("padlocked_door")
        );
        reg.register(
            makeType(
                LockedDoorTile::new, ModBlocks.LOCKED_IRON_DOOR, ModBlocks.LOCKED_OAK_DOOR, ModBlocks.LOCKED_BIRCH_DOOR,
                ModBlocks.LOCKED_SPRUCE_DOOR, ModBlocks.LOCKED_JUNGLE_DOOR, ModBlocks.LOCKED_ACACIA_DOOR,
                ModBlocks.LOCKED_DARK_OAK_DOOR
            ).setRegistryName("locked_door")
        );
    }

    private static <T extends TileEntity> TileEntityType<T> makeType(Supplier<T> factory, Block... validBlocks) {
        return Builder.create(factory, validBlocks).build(null);
    }
}
