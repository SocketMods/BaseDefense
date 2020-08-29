package sciwhiz12.basedefense.item.lock;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import sciwhiz12.basedefense.api.ITooltipInfo;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.tileentity.PadlockedDoorTile;
import sciwhiz12.basedefense.util.ItemHelper;

import java.util.List;

import static sciwhiz12.basedefense.Reference.Capabilities.LOCK;
import static sciwhiz12.basedefense.block.PadlockedDoorBlock.*;

public abstract class AbstractPadlockItem extends Item implements IColorable {
    public AbstractPadlockItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        stack.getCapability(LOCK).filter(ITooltipInfo.class::isInstance)
                .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        if (!flagIn.isAdvanced()) { return; }
        ItemHelper.addColorInformation(stack, tooltip);
    }

    @Override
    public abstract ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt);

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        final World world = context.getWorld();
        final BlockPos pos = context.getPos();
        final BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof DoorBlock && !state.get(DoorBlock.OPEN) && !state.get(DoorBlock.POWERED)) {
            final PadlockedDoorBlock block = PadlockedDoorBlock.getReplacement(state.getBlock());
            if (block == null || !block.isValidLock(stack)) { return ActionResultType.PASS; }
            if (!world.isRemote) {
                final boolean isLower = state.get(PadlockedDoorBlock.HALF) == DoubleBlockHalf.LOWER;
                final BlockPos offPos = isLower ? pos.up() : pos.down();
                final BlockState offState = world.getBlockState(offPos);

                final Direction facing = state.get(DoorBlock.FACING);
                final DoorHingeSide hinge = state.get(DoorBlock.HINGE);
                DoorSide side = DoorSide.getSideForDirection(facing, context.getFace());
                final BlockState defState = block.getDefaultState().with(HINGE, hinge).with(FACING, facing);

                final BlockState newState = defState.with(HALF, state.get(DoorBlock.HALF)).with(SIDE, side);
                final BlockState newOffState = defState.with(HALF, offState.get(DoorBlock.HALF)).with(SIDE, side);

                final ItemStack copy = stack.copy();
                copy.setCount(1);
                stack.setCount(stack.getCount() - 1);

                int flags =
                        Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.RERENDER_MAIN_THREAD | Constants.BlockFlags.UPDATE_NEIGHBORS | Constants.BlockFlags.NO_NEIGHBOR_DROPS;
                world.setBlockState(pos, newState, flags);
                world.setBlockState(offPos, newOffState, flags);
                TileEntity te = world.getTileEntity(isLower ? pos : offPos);
                if (te instanceof PadlockedDoorTile) { ((PadlockedDoorTile) te).setLockStack(copy); }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
