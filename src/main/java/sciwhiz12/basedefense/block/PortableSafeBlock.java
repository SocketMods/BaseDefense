package sciwhiz12.basedefense.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import sciwhiz12.basedefense.item.IContainsLockItem;
import sciwhiz12.basedefense.item.LockedBlockItem;
import sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;
import sciwhiz12.basedefense.util.UnlockHelper;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.GRAY;

public class PortableSafeBlock extends Block implements IWaterLoggable {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final ResourceLocation CONTENTS = new ResourceLocation("contents");
    private static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 0.5D, 15.0D, 15.0D, 15.0D);
    private static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.5D, 15.0D, 15.0D);
    private static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.5D);
    private static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(0.5D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    public PortableSafeBlock() {
        super(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(40F, 1200F));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
            default:
            case NORTH:
                return NORTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case WEST:
                return WEST_SHAPE;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof PortableSafeTileEntity) {
            PortableSafeTileEntity safe = (PortableSafeTileEntity) te;
            if (!worldIn.isRemote && player.isCreative() && !safe.isEmpty()) {
                ItemStack stack = this.getItem(worldIn, pos, state);

                CompoundNBT compoundnbt = safe.writeData(new CompoundNBT());
                if (!compoundnbt.isEmpty()) {
                    stack.setTagInfo("BlockEntityTag", compoundnbt);
                }
                if (safe.hasCustomName()) {
                    stack.setDisplayName(safe.getCustomName());
                }
                if (stack.getItem() instanceof IContainsLockItem) {
                    ((IContainsLockItem) stack.getItem()).setLockStack(stack, safe.getLockStack());
                }

                ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D,
                        (double) pos.getZ() + 0.5D, stack);
                itementity.setDefaultPickupDelay();
                worldIn.addEntity(itementity);
            }
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        builder = builder.withDynamicDrop(CONTENTS, (context, stackConsumer) -> {
            TileEntity te = context.get(LootParameters.BLOCK_ENTITY);
            if (te instanceof PortableSafeTileEntity) {
                PortableSafeTileEntity safe = (PortableSafeTileEntity) te;
                IItemHandler inv = safe.getInventory();
                for (int i = 0; i < inv.getSlots(); i++) {
                    stackConsumer.accept(inv.getStackInSlot(i));
                }
            }
        });
        return super.getDrops(state, builder);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof LockedBlockItem) {
            LockedBlockItem item = (LockedBlockItem) stack.getItem();
            TileEntity te = worldIn.getTileEntity(pos);
            if (item.hasLockStack(stack) && te instanceof PortableSafeTileEntity) {
                PortableSafeTileEntity safeTE = (PortableSafeTileEntity) te;
                ItemStack lockStack = item.getLockStack(stack);
                safeTE.setCustomName(lockStack.getDisplayName());
                safeTE.setLockStack(lockStack);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        worldIn.updateComparatorOutputLevel(pos, this);
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
            BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            INamedContainerProvider provider = this.getContainer(state, worldIn, pos);
            TileEntity te = worldIn.getTileEntity(pos);
            if (provider != null && te instanceof PortableSafeTileEntity) {
                PortableSafeTileEntity safeTE = (PortableSafeTileEntity) te;
                ItemStack heldItem = player.getHeldItem(handIn);
                if (safeTE.getNumPlayersUsing() > 0 || (!heldItem.isEmpty() && UnlockHelper
                        .checkUnlock(heldItem, safeTE, worldIn, pos, player, true))) {
                    player.openContainer(provider);
                }
            }
            return ActionResultType.CONSUME;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof PortableSafeTileEntity ? (PortableSafeTileEntity) te : null;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip,
            ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
        if (nbt != null && nbt.contains("Items", Constants.NBT.TAG_LIST)) {
            if (nbt.getInt("Size") > 0) {
                tooltip.add(new TranslationTextComponent("tooltip.basedefense.contains_items").mergeStyle(GRAY));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
            PlayerEntity player) {
        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        PortableSafeTileEntity te = (PortableSafeTileEntity) world.getTileEntity(pos);
        if (te != null) {
            CompoundNBT compoundnbt = te.writeData(new CompoundNBT(), false);
            if (!compoundnbt.isEmpty()) {
                stack.setTagInfo("BlockEntityTag", compoundnbt);
            }
            if (stack.getItem() instanceof IContainsLockItem) {
                ((IContainsLockItem) stack.getItem()).setLockStack(stack, te.getLockStack());
            }
        }
        return stack;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PortableSafeTileEntity();
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null) {
            return ItemHandlerHelper.calcRedstoneFromInventory(
                    te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new));
        }
        return 0;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
