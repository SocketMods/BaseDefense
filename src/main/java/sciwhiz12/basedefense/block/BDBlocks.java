package sciwhiz12.basedefense.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.tileentity.TestLockTile;

public class BDBlocks {
    public static final DeferredRegister<Block> BLOCK_REGISTER = new DeferredRegister<>(
            ForgeRegistries.BLOCKS, BaseDefense.MODID
    );

    public static final RegistryObject<Block> TEST_LOCK_BLOCK = BLOCK_REGISTER.register(
            "test_lock_block", () -> new TestLockBlock()
    );

    public static final DeferredRegister<TileEntityType<?>> TE_REGISTER = new DeferredRegister<>(
            ForgeRegistries.TILE_ENTITIES, BaseDefense.MODID
    );

    public static final RegistryObject<TileEntityType<?>> TEST_LOCK_TILE = TE_REGISTER.register(
            "test_lock_block", () -> TileEntityType.Builder.create(
                    TestLockTile::new, TEST_LOCK_BLOCK.get()
            ).build(null)
    );

    public static void setupRenderLayer() {
        RenderTypeLookup.setRenderLayer(BDBlocks.TEST_LOCK_BLOCK.get(), RenderType.getSolid());
    }
}
