package sciwhiz12.basedefense.event;

import static sciwhiz12.basedefense.block.PadlockedDoorBlock.FACING;
import static sciwhiz12.basedefense.block.PadlockedDoorBlock.HALF;
import static sciwhiz12.basedefense.block.PadlockedDoorBlock.HINGE;
import static sciwhiz12.basedefense.block.PadlockedDoorBlock.SIDE;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.block.PadlockedDoorBlock.DoorSide;
import sciwhiz12.basedefense.init.ModBlocks;
import sciwhiz12.basedefense.tileentity.LockableTile;

@EventBusSubscriber(bus = Bus.FORGE, modid = BaseDefense.MODID)
public class DoorLockerHandler {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide() != LogicalSide.SERVER) { return; }
        ItemStack stack = event.getItemStack();
        final PadlockedDoorBlock block = (PadlockedDoorBlock) ModBlocks.PADLOCKED_DOOR.get();
        if (stack.isEmpty() || !block.isValidLock(stack) || stack.getCount() <= 0) { return; }
        final World world = event.getWorld();
        final BlockPos pos = event.getPos();
        final BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof DoorBlock && !state.get(DoorBlock.OPEN) && !state.get(DoorBlock.POWERED)) {
            final boolean isLower = state.get(PadlockedDoorBlock.HALF) == DoubleBlockHalf.LOWER;
            final BlockPos offPos = isLower ? pos.up() : pos.down();
            final BlockState offState = world.getBlockState(offPos);

            final Direction facing = state.get(DoorBlock.FACING);
            final DoorHingeSide hinge = state.get(DoorBlock.HINGE);
            DoorSide side = DoorSide.getSideForDirection(facing, event.getFace());
            final BlockState defState = block.getDefaultState().with(HINGE, hinge).with(FACING, facing);

            final BlockState newState = defState.with(HALF, state.get(DoorBlock.HALF)).with(SIDE, side);
            final BlockState newOffState = defState.with(HALF, offState.get(DoorBlock.HALF)).with(SIDE, side);

            final LockableTile te = (LockableTile) block.createTileEntity(state, world);
            final ItemStack copy = stack.copy();
            copy.setCount(1);
            stack.setCount(stack.getCount() - 1);
            te.setLock(copy);

            int flags = Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.RERENDER_MAIN_THREAD | Constants.BlockFlags.UPDATE_NEIGHBORS
                    | Constants.BlockFlags.NO_NEIGHBOR_DROPS;
            world.setBlockState(pos, newState, flags);
            world.setBlockState(offPos, newOffState, flags);
            world.setTileEntity(isLower ? pos : offPos, te);
        }
    }
}
