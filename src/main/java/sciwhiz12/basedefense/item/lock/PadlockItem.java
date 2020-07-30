package sciwhiz12.basedefense.item.lock;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import sciwhiz12.basedefense.api.ITooltipInfo;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.capabilities.CodedLock;
import sciwhiz12.basedefense.capabilities.SerializableCapabilityProvider;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.tileentity.LockableTile;
import sciwhiz12.basedefense.tileentity.PadlockedDoorTile;
import sciwhiz12.basedefense.util.ItemHelper;

import java.util.List;

import static sciwhiz12.basedefense.Reference.Capabilities.*;
import static sciwhiz12.basedefense.Reference.ITEM_GROUP;
import static sciwhiz12.basedefense.block.PadlockedDoorBlock.*;

public class PadlockItem extends Item implements IColorable {
    public PadlockItem() {
        super(new Item.Properties().maxDamage(0).group(ITEM_GROUP));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        stack.getCapability(LOCK).filter(ITooltipInfo.class::isInstance)
                .ifPresent(lock -> ((ITooltipInfo) lock).addInformation(tooltip, flagIn.isAdvanced()));
        if (!flagIn.isAdvanced()) { return; }
        ItemHelper.addColorInformation(stack, tooltip);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new SerializableCapabilityProvider<>(() -> new CodedLock() {
            @Override
            public void onRemove(IKey key, IWorldPosCallable worldPos, PlayerEntity player) {
                worldPos.consume((world, pos) -> {
                    TileEntity te = world.getTileEntity(pos);
                    if (te instanceof LockableTile) {
                        LockableTile lockTile = (LockableTile) te;
                        ItemHandlerHelper.giveItemToPlayer(player, lockTile.getLockStack());
                        lockTile.setLockStack(ItemStack.EMPTY);
                    }
                });
            }
        }, CONTAINS_CODE, CODE_HOLDER, LOCK);
    }

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        return ItemHelper.getItemShareTag(stack, CODE_HOLDER);
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        ItemHelper.readItemShareTag(stack, nbt, CODE_HOLDER);
    }

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

                int flags = Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.RERENDER_MAIN_THREAD
                        | Constants.BlockFlags.UPDATE_NEIGHBORS | Constants.BlockFlags.NO_NEIGHBOR_DROPS;
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
