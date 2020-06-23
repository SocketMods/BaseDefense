package sciwhiz12.basedefense.init;

import static sciwhiz12.basedefense.BaseDefense.CLIENT;
import static sciwhiz12.basedefense.BaseDefense.LOG;

import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.tileentity.LockedDoorTile;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD, modid = BaseDefense.MODID)
public class ModColors {

    public static final IItemColor ITEM_COLOR = (stack, tintIndex) -> {
        if (stack.getItem() instanceof IColorable && tintIndex >= 2) {
            return ((IColorable) stack.getItem()).getColor(stack, tintIndex - 2);
        }
        return -1;
    };

    public static final IBlockColor LOCKED_DOOR_COLOR = (state, world, pos, tintIndex) -> {
        if (world != null && pos != null && state.getBlock() instanceof LockedDoorBlock) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof LockedDoorTile && ((LockedDoorTile) tile).hasColors()) {
                int[] colors = ((LockedDoorTile) tile).getColors();
                // 0 : NONE, 1 : ind. 0 ; 2 : inds. 1, 2 ;
                if (colors.length - 1 >= tintIndex) { return colors[tintIndex]; }
            }
        }
        return -1;
    };

    @SubscribeEvent
    static void registerItemColors(ColorHandlerEvent.Item event) {
        LOG.debug(CLIENT, "Registering item colors");
        event.getItemColors().register(
            ITEM_COLOR, ModItems.KEY, ModItems.LOCK_CORE, ModItems.PADLOCK, ModItems.BROKEN_PADLOCK
        );
    }

    @SubscribeEvent
    static void registerBlockColors(ColorHandlerEvent.Block event) {
        LOG.debug(CLIENT, "Registering block colors");
        event.getBlockColors().register(
            LOCKED_DOOR_COLOR, ModBlocks.LOCKED_IRON_DOOR, ModBlocks.LOCKED_OAK_DOOR, ModBlocks.LOCKED_BIRCH_DOOR,
            ModBlocks.LOCKED_SPRUCE_DOOR, ModBlocks.LOCKED_JUNGLE_DOOR, ModBlocks.LOCKED_ACACIA_DOOR,
            ModBlocks.LOCKED_DARK_OAK_DOOR
        );
    }
}
