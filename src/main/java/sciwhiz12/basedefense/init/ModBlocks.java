package sciwhiz12.basedefense.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.block.KeysmithBlock;
import sciwhiz12.basedefense.block.LocksmithBlock;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.block.TestLockBlock;

public class ModBlocks {
    public static final DeferredRegister<Block> REGISTER = new DeferredRegister<>(ForgeRegistries.BLOCKS, BaseDefense.MODID);

    public static final RegistryObject<Block> TEST_LOCK_BLOCK = REGISTER.register(
        "test_lock_block", () -> new TestLockBlock()
    );
    public static final RegistryObject<Block> KEYSMITH_BLOCK = REGISTER.register(
        "keysmith_table", () -> new KeysmithBlock()
    );
    public static final RegistryObject<Block> LOCKSMITH_BLOCK = REGISTER.register(
        "locksmith_table", () -> new LocksmithBlock()
    );

    public static final RegistryObject<Block> PADLOCKED_IRON_DOOR = REGISTER.register(
        "padlocked_iron_door", () -> new PadlockedDoorBlock(Blocks.IRON_DOOR)
    );
    public static final RegistryObject<Block> PADLOCKED_OAK_DOOR = REGISTER.register(
        "padlocked_oak_door", () -> new PadlockedDoorBlock(Blocks.OAK_DOOR)
    );
    public static final RegistryObject<Block> PADLOCKED_BIRCH_DOOR = REGISTER.register(
        "padlocked_birch_door", () -> new PadlockedDoorBlock(Blocks.BIRCH_DOOR)
    );
    public static final RegistryObject<Block> PADLOCKED_SPRUCE_DOOR = REGISTER.register(
        "padlocked_spruce_door", () -> new PadlockedDoorBlock(Blocks.SPRUCE_DOOR)
    );
    public static final RegistryObject<Block> PADLOCKED_JUNGLE_DOOR = REGISTER.register(
        "padlocked_jungle_door", () -> new PadlockedDoorBlock(Blocks.JUNGLE_DOOR)
    );
    public static final RegistryObject<Block> PADLOCKED_ACACIA_DOOR = REGISTER.register(
        "padlocked_acacia_door", () -> new PadlockedDoorBlock(Blocks.ACACIA_DOOR)
    );
    public static final RegistryObject<Block> PADLOCKED_DARK_OAK_DOOR = REGISTER.register(
        "padlocked_dark_oak_door", () -> new PadlockedDoorBlock(Blocks.DARK_OAK_DOOR)
    );

    public static final RegistryObject<Block> LOCKED_IRON_DOOR = REGISTER.register(
        "locked_iron_door", () -> new LockedDoorBlock(Blocks.IRON_DOOR)
    );
    public static final RegistryObject<Block> LOCKED_OAK_DOOR = REGISTER.register(
        "locked_oak_door", () -> new LockedDoorBlock(Blocks.OAK_DOOR)
    );
    public static final RegistryObject<Block> LOCKED_BIRCH_DOOR = REGISTER.register(
        "locked_birch_door", () -> new LockedDoorBlock(Blocks.BIRCH_DOOR)
    );
    public static final RegistryObject<Block> LOCKED_SPRUCE_DOOR = REGISTER.register(
        "locked_spruce_door", () -> new LockedDoorBlock(Blocks.SPRUCE_DOOR)
    );
    public static final RegistryObject<Block> LOCKED_JUNGLE_DOOR = REGISTER.register(
        "locked_jungle_door", () -> new LockedDoorBlock(Blocks.JUNGLE_DOOR)
    );
    public static final RegistryObject<Block> LOCKED_ACACIA_DOOR = REGISTER.register(
        "locked_acacia_door", () -> new LockedDoorBlock(Blocks.ACACIA_DOOR)
    );
    public static final RegistryObject<Block> LOCKED_DARK_OAK_DOOR = REGISTER.register(
        "locked_dark_oak_door", () -> new LockedDoorBlock(Blocks.DARK_OAK_DOOR)
    );

    public static void setupRenderLayer() {
        final RenderType solid = RenderType.getSolid();
        RenderTypeLookup.setRenderLayer(ModBlocks.TEST_LOCK_BLOCK.get(), solid);
        RenderTypeLookup.setRenderLayer(ModBlocks.KEYSMITH_BLOCK.get(), solid);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKSMITH_BLOCK.get(), solid);
        final RenderType cutoutMipped = RenderType.getCutoutMipped();
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_IRON_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_OAK_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_BIRCH_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_SPRUCE_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_JUNGLE_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_ACACIA_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_DARK_OAK_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_IRON_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_OAK_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_BIRCH_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_SPRUCE_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_JUNGLE_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_ACACIA_DOOR.get(), cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_DARK_OAK_DOOR.get(), cutoutMipped);
    }
}
