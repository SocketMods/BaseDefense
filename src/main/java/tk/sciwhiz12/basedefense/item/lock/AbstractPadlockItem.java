package tk.sciwhiz12.basedefense.item.lock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.api.ITooltipInfo;
import tk.sciwhiz12.basedefense.block.PadlockedDoorBlock;
import tk.sciwhiz12.basedefense.item.IColorable;
import tk.sciwhiz12.basedefense.tileentity.PadlockedDoorTile;
import tk.sciwhiz12.basedefense.util.ItemHelper;

import java.util.List;

import static tk.sciwhiz12.basedefense.Reference.Capabilities.LOCK;

public abstract class AbstractPadlockItem extends Item implements IColorable {
    public AbstractPadlockItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
                                TooltipFlag flagIn) {
        stack.getCapability(LOCK).filter(ITooltipInfo.class::isInstance)
            .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        if (!flagIn.isAdvanced()) {
            return;
        }
        ItemHelper.addColorInformation(stack, tooltip);
    }

    @Override
    public abstract ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt);

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        final Level world = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof DoorBlock && !state.getValue(DoorBlock.OPEN) && !state.getValue(DoorBlock.POWERED)) {
            final PadlockedDoorBlock block = PadlockedDoorBlock.getReplacement(state.getBlock());
            if (block == null || !block.isValidLock(stack)) {
                return InteractionResult.PASS;
            }
            if (!world.isClientSide) {
                final boolean isLower = state.getValue(PadlockedDoorBlock.HALF) == DoubleBlockHalf.LOWER;
                final BlockPos offPos = isLower ? pos.above() : pos.below();
                final BlockState offState = world.getBlockState(offPos);

                final Direction facing = state.getValue(DoorBlock.FACING);
                final DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);
                PadlockedDoorBlock.DoorSide side = PadlockedDoorBlock.DoorSide.getSideForDirection(facing, context.getClickedFace());
                final BlockState defState = block.defaultBlockState().setValue(PadlockedDoorBlock.HINGE, hinge).setValue(PadlockedDoorBlock.FACING, facing);

                final BlockState newState = defState.setValue(PadlockedDoorBlock.HALF, state.getValue(DoorBlock.HALF)).setValue(PadlockedDoorBlock.SIDE, side);
                final BlockState newOffState = defState.setValue(PadlockedDoorBlock.HALF, offState.getValue(DoorBlock.HALF)).setValue(PadlockedDoorBlock.SIDE, side);

                final ItemStack copy = stack.copy();
                copy.setCount(1);
                stack.setCount(stack.getCount() - 1);

                int flags =
                    Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS;
                world.setBlock(pos, newState, flags);
                world.setBlock(offPos, newOffState, flags);
                @Nullable BlockEntity te = world.getBlockEntity(isLower ? pos : offPos);
                if (te instanceof PadlockedDoorTile) {
                    ((PadlockedDoorTile) te).setLockStack(copy);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
