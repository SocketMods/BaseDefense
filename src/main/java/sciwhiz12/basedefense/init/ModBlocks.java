package sciwhiz12.basedefense.init;

import static sciwhiz12.basedefense.util.Util.Null;

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
import sciwhiz12.basedefense.block.*;

@ObjectHolder(BaseDefense.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModBlocks {

    public static final TestLockBlock TEST_LOCK_BLOCK = Null();
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
