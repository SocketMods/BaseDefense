package sciwhiz12.basedefense.init;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.block.KeysmithBlock;
import sciwhiz12.basedefense.block.LocksmithBlock;
import sciwhiz12.basedefense.block.TestLockBlock;
import sciwhiz12.basedefense.container.KeysmithContainer;
import sciwhiz12.basedefense.container.LocksmithContainer;
import sciwhiz12.basedefense.tileentity.TestLockTile;

public class BDBlocks {
    public static final DeferredRegister<Block> BLOCK = new DeferredRegister<>(
            ForgeRegistries.BLOCKS, BaseDefense.MODID
    );
    public static final DeferredRegister<TileEntityType<?>> TE = new DeferredRegister<>(
            ForgeRegistries.TILE_ENTITIES, BaseDefense.MODID
    );
    public static final DeferredRegister<ContainerType<?>> CONTAINER = new DeferredRegister<>(
            ForgeRegistries.CONTAINERS, BaseDefense.MODID
    );

    public static final RegistryObject<Block> TEST_LOCK_BLOCK = BLOCK.register(
            "test_lock_block", () -> new TestLockBlock()
    );
    public static final RegistryObject<Block> KEYSMITH_BLOCK = BLOCK.register(
            "keysmith_table", () -> new KeysmithBlock()
    );
    public static final RegistryObject<Block> LOCKSMITH_BLOCK = BLOCK.register(
            "locksmith_table", () -> new LocksmithBlock()
    );

    public static final RegistryObject<TileEntityType<TestLockTile>> TEST_LOCK_TILE = TE.register(
            "test_lock_tile", () -> TileEntityType.Builder.create(
                    TestLockTile::new, TEST_LOCK_BLOCK.get()
            ).build(null)
    );

    public static final RegistryObject<ContainerType<KeysmithContainer>> KEYSMITH_CONTAINER = CONTAINER
            .register("keysmith_table", () -> new ContainerType<>(KeysmithContainer::new));
    public static final RegistryObject<ContainerType<LocksmithContainer>> LOCKSMITH_CONTAINER = CONTAINER
            .register("locksmith_table", () -> new ContainerType<>(LocksmithContainer::new));

    public static void setupRenderLayer() {
        RenderType solid = RenderType.getSolid();
        RenderTypeLookup.setRenderLayer(BDBlocks.TEST_LOCK_BLOCK.get(), solid);
        RenderTypeLookup.setRenderLayer(BDBlocks.KEYSMITH_BLOCK.get(), solid);
        RenderTypeLookup.setRenderLayer(BDBlocks.LOCKSMITH_BLOCK.get(), solid);
    }
}
