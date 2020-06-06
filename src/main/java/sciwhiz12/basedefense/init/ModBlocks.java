package sciwhiz12.basedefense.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.block.KeysmithBlock;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.block.LocksmithBlock;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.block.TestLockBlock;

@ObjectHolder(BaseDefense.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModBlocks {

    public static final Block TEST_LOCK_BLOCK = null;
    public static final Block KEYSMITH_TABLE = null;
    public static final Block LOCKSMITH_TABLE = null;

    public static final Block PADLOCKED_IRON_DOOR = null;
    public static final Block PADLOCKED_OAK_DOOR = null;
    public static final Block PADLOCKED_BIRCH_DOOR = null;
    public static final Block PADLOCKED_SPRUCE_DOOR = null;
    public static final Block PADLOCKED_JUNGLE_DOOR = null;
    public static final Block PADLOCKED_ACACIA_DOOR = null;
    public static final Block PADLOCKED_DARK_OAK_DOOR = null;

    public static final Block LOCKED_IRON_DOOR = null;
    public static final Block LOCKED_OAK_DOOR = null;
    public static final Block LOCKED_BIRCH_DOOR = null;
    public static final Block LOCKED_SPRUCE_DOOR = null;
    public static final Block LOCKED_JUNGLE_DOOR = null;
    public static final Block LOCKED_ACACIA_DOOR = null;
    public static final Block LOCKED_DARK_OAK_DOOR = null;

    @SubscribeEvent
    static void onRegister(RegistryEvent.Register<Block> event) {
        BaseDefense.LOG.debug("Registering blocks");
        final IForgeRegistry<Block> reg = event.getRegistry();

        reg.register(new TestLockBlock().setRegistryName("test_lock_block"));
        reg.register(new KeysmithBlock().setRegistryName("keysmith_table"));
        reg.register(new LocksmithBlock().setRegistryName("locksmith_table"));

        Block[] doorBlocks = { Blocks.IRON_DOOR, Blocks.OAK_DOOR, Blocks.BIRCH_DOOR, Blocks.SPRUCE_DOOR, Blocks.JUNGLE_DOOR,
                Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR };
        for (Block b : doorBlocks) {
            reg.register(new PadlockedDoorBlock(b).setRegistryName("padlocked_" + b.getRegistryName().getPath()));
            reg.register(new LockedDoorBlock(b).setRegistryName("locked_" + b.getRegistryName().getPath()));
        }
    }

    public static void setupRenderLayer() {
        final RenderType solid = RenderType.getSolid();
        RenderTypeLookup.setRenderLayer(ModBlocks.TEST_LOCK_BLOCK, solid);
        RenderTypeLookup.setRenderLayer(ModBlocks.KEYSMITH_TABLE, solid);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKSMITH_TABLE, solid);
        final RenderType cutoutMipped = RenderType.getCutoutMipped();
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_IRON_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_OAK_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_BIRCH_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_SPRUCE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_JUNGLE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_ACACIA_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.PADLOCKED_DARK_OAK_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_IRON_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_OAK_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_BIRCH_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_SPRUCE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_JUNGLE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_ACACIA_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(ModBlocks.LOCKED_DARK_OAK_DOOR, cutoutMipped);
    }
}
