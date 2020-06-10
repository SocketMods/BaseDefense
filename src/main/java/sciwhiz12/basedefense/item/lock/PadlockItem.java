package sciwhiz12.basedefense.item.lock;

import static sciwhiz12.basedefense.block.PadlockedDoorBlock.FACING;
import static sciwhiz12.basedefense.block.PadlockedDoorBlock.HALF;
import static sciwhiz12.basedefense.block.PadlockedDoorBlock.HINGE;
import static sciwhiz12.basedefense.block.PadlockedDoorBlock.SIDE;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.block.PadlockedDoorBlock.DoorSide;
import sciwhiz12.basedefense.capabilities.CodedLock;
import sciwhiz12.basedefense.capabilities.SerializableCapabilityProvider;
import sciwhiz12.basedefense.init.ModCapabilities;
import sciwhiz12.basedefense.item.IColorable;
import sciwhiz12.basedefense.tileentity.LockableTile;
import sciwhiz12.basedefense.tileentity.PadlockedDoorTile;
import sciwhiz12.basedefense.util.Util;

public class PadlockItem extends Item implements IColorable {
    private static final IItemPropertyGetter COLOR_GETTER = (stack, world, livingEntity) -> {
        CompoundNBT tag = stack.getChildTag("display");
        if (tag != null && tag.contains("colors")) { return (float) tag.getIntArray("colors").length; }
        return 0.0F;
    };

    public PadlockItem() {
        super(new Item.Properties().maxDamage(0));
        this.addPropertyOverride(new ResourceLocation("colors"), COLOR_GETTER);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!flagIn.isAdvanced()) { return; }
        Util.addCodeInformation(stack, tooltip);
        Util.addColorInformation(stack, tooltip);
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
        }, ModCapabilities.CONTAINS_CODE, ModCapabilities.CODE_HOLDER, ModCapabilities.LOCK);
    }

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        return Util.getItemShareTag(stack, ModCapabilities.CODE_HOLDER);
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundNBT nbt) {
        Util.readItemShareTag(stack, nbt, ModCapabilities.CODE_HOLDER);
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
