package sciwhiz12.basedefense.init;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.block.KeysmithBlock;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.block.LocksmithBlock;
import sciwhiz12.basedefense.block.TestLockBlock;

public class ModBlocks {
    public static final DeferredRegister<Block> REGISTER = new DeferredRegister<>(
        ForgeRegistries.BLOCKS, BaseDefense.MODID
    );

    public static final RegistryObject<Block> TEST_LOCK_BLOCK = REGISTER.register(
        "test_lock_block", () -> new TestLockBlock()
    );
    public static final RegistryObject<Block> PADLOCKED_DOOR = REGISTER.register(
        "padlocked_door", () -> new PadlockedDoorBlock()
    );
    public static final RegistryObject<Block> KEYSMITH_BLOCK = REGISTER.register(
        "keysmith_table", () -> new KeysmithBlock()
    );
    public static final RegistryObject<Block> LOCKSMITH_BLOCK = REGISTER.register(
        "locksmith_table", () -> new LocksmithBlock()
    );

    public static void setupRenderLayer() {
        RenderType solid = RenderType.getSolid();
        RenderTypeLookup.setRenderLayer(ModBlocks.TEST_LOCK_BLOCK.get(), solid);
        RenderTypeLookup.setRenderLayer(ModBlocks.KEYSMITH_BLOCK.get(), solid);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKSMITH_BLOCK.get(), solid);
        RenderTypeLookup.setRenderLayer(
            ModBlocks.PADLOCKED_DOOR.get(), RenderType.getCutoutMipped()
        );
    }
}
