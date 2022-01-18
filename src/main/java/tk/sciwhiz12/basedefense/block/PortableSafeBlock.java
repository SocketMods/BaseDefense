package tk.sciwhiz12.basedefense.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import tk.sciwhiz12.basedefense.Reference;
import tk.sciwhiz12.basedefense.item.IContainsLockItem;
import tk.sciwhiz12.basedefense.item.LockedBlockItem;
import tk.sciwhiz12.basedefense.tileentity.PortableSafeTileEntity;
import tk.sciwhiz12.basedefense.util.UnlockHelper;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.ChatFormatting.GRAY;

public class PortableSafeBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final ResourceLocation CONTENTS = new ResourceLocation("contents");
    private static final VoxelShape NORTH_SHAPE = Block.box(1.0D, 0.0D, 0.5D, 15.0D, 15.0D, 15.0D);
    private static final VoxelShape EAST_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.5D, 15.0D, 15.0D);
    private static final VoxelShape SOUTH_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.5D);
    private static final VoxelShape WEST_SHAPE = Block.box(0.5D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    public PortableSafeBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).strength(40F, 1200F));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof PortableSafeTileEntity) {
            PortableSafeTileEntity safe = (PortableSafeTileEntity) te;
            if (!worldIn.isClientSide && player.isCreative() && !safe.isEmpty()) {
                @SuppressWarnings("deprecation")
                ItemStack stack = this.getCloneItemStack(worldIn, pos, state);

                CompoundTag compoundnbt = new CompoundTag();
                safe.writeData(compoundnbt, false);
                if (!compoundnbt.isEmpty()) {
                    stack.addTagElement("BlockEntityTag", compoundnbt);
                }
                if (safe.hasCustomName()) {
                    stack.setHoverName(safe.getCustomName());
                }
                if (stack.getItem() instanceof IContainsLockItem) {
                    ((IContainsLockItem) stack.getItem()).setLockStack(stack, safe.getLockStack());
                }

                ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D,
                        (double) pos.getZ() + 0.5D, stack);
                itementity.setDefaultPickUpDelay();
                worldIn.addFreshEntity(itementity);
            }
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        builder = builder.withDynamicDrop(CONTENTS, (context, stackConsumer) -> {
            BlockEntity te = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
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
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof LockedBlockItem) {
            LockedBlockItem item = (LockedBlockItem) stack.getItem();
            BlockEntity te = worldIn.getBlockEntity(pos);
            if (item.hasLockStack(stack) && te instanceof PortableSafeTileEntity) {
                PortableSafeTileEntity safeTE = (PortableSafeTileEntity) te;
                ItemStack lockStack = item.getLockStack(stack);
                safeTE.setCustomName(lockStack.getHoverName());
                safeTE.setLockStack(lockStack);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        worldIn.updateNeighbourForOutputSignal(pos, this);
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
            BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            MenuProvider provider = this.getMenuProvider(state, worldIn, pos);
            BlockEntity te = worldIn.getBlockEntity(pos);
            if (provider != null && te instanceof PortableSafeTileEntity) {
                PortableSafeTileEntity safeTE = (PortableSafeTileEntity) te;
                ItemStack heldItem = player.getItemInHand(handIn);
                if (safeTE.getNumPlayersUsing() > 0 || (!heldItem.isEmpty() && UnlockHelper
                        .checkUnlock(heldItem, safeTE, worldIn, pos, player, true))) {
                    player.openMenu(provider);
                }
            }
            return InteractionResult.CONSUME;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        super.triggerEvent(state, worldIn, pos, id, param);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity != null && tileentity.triggerEvent(id, param);
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        return te instanceof PortableSafeTileEntity ? (PortableSafeTileEntity) te : null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip,
            TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null && nbt.contains("Items", Tag.TAG_LIST)) {
            if (nbt.getInt("Size") > 0) {
                tooltip.add(new TranslatableComponent("tooltip.basedefense.contains_items").withStyle(GRAY));
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
            Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, world, pos, player);
        PortableSafeTileEntity te = (PortableSafeTileEntity) world.getBlockEntity(pos);
        if (te != null) {
            CompoundTag compoundnbt = new CompoundTag();
            te.writeData(compoundnbt, false);
            if (!compoundnbt.isEmpty()) {
                stack.addTagElement("BlockEntityTag", compoundnbt);
            }
            if (stack.getItem() instanceof IContainsLockItem) {
                ((IContainsLockItem) stack.getItem()).setLockStack(stack, te.getLockStack());
            }
        }
        return stack;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortableSafeTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, Reference.TileEntities.PORTABLE_SAFE, PortableSafeTileEntity::tick);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> type, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == type ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te != null) {
            return ItemHandlerHelper.calcRedstoneFromInventory(
                    te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new));
        }
        return 0;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
