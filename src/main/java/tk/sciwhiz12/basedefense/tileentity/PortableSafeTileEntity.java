package tk.sciwhiz12.basedefense.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import tk.sciwhiz12.basedefense.Reference.TileEntities;
import tk.sciwhiz12.basedefense.block.PortableSafeBlock;
import tk.sciwhiz12.basedefense.container.PortableSafeContainer;
import tk.sciwhiz12.basedefense.util.Util;

import javax.annotation.Nullable;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class PortableSafeTileEntity extends LockableTile implements MenuProvider, Nameable {
    protected float doorAngle;
    protected float prevDoorAngle;
    protected int numPlayersUsing;
    private int ticksSinceSync;
    private final ItemStackHandler inv = new ItemStackHandler(18) {
        protected void onContentsChanged(int slot) {
            PortableSafeTileEntity.this.setChanged();
        }
    };
    private LazyOptional<IItemHandler> invHandler;
    private Component customName;

    public PortableSafeTileEntity(BlockPos pos, BlockState state) {
        super(TileEntities.PORTABLE_SAFE, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PortableSafeTileEntity blockEntity) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        ++blockEntity.ticksSinceSync;
        blockEntity.numPlayersUsing = calculatePlayersUsingSync(level, blockEntity, blockEntity.ticksSinceSync, x, y, z,
            blockEntity.numPlayersUsing);
        blockEntity.prevDoorAngle = blockEntity.doorAngle;
        if (blockEntity.numPlayersUsing > 0 && blockEntity.doorAngle == 0.0F) {
            blockEntity.playSound(SoundEvents.CHEST_OPEN); // TODO: change to own sound
        }

        if (blockEntity.numPlayersUsing == 0 && blockEntity.doorAngle > 0.0F || blockEntity.numPlayersUsing > 0 && blockEntity.doorAngle < 1.0F) {
            float oldAngle = blockEntity.doorAngle;
            if (blockEntity.numPlayersUsing > 0) {
                blockEntity.doorAngle += 0.1F;
            } else {
                blockEntity.doorAngle -= 0.1F;
            }

            if (blockEntity.doorAngle > 1.0F) {
                blockEntity.doorAngle = 1.0F;
            }

            if (blockEntity.doorAngle < 0.5F && oldAngle >= 0.5F) {
                blockEntity.playSound(SoundEvents.CHEST_CLOSE); // TODO: change to own sound
            }

            if (blockEntity.doorAngle < 0.0F) {
                blockEntity.doorAngle = 0.0F;
            }
        }

    }

    public static int calculatePlayersUsingSync(Level worldIn, BlockEntity tileEntityIn, int ticksSinceSync, int posX,
                                                int posY, int posZ, int numPlayersUsing) {
        if (!worldIn.isClientSide && numPlayersUsing != 0 && (ticksSinceSync + posX + posY + posZ) % 200 == 0) {
            numPlayersUsing = calculatePlayersUsing(worldIn, tileEntityIn, posX, posY, posZ);
        }

        return numPlayersUsing;
    }

    public static int calculatePlayersUsing(Level worldIn, BlockEntity tileEntity, int posX, int posY, int posZ) {
        int playerCount = 0;

        for (Player player : worldIn.getEntitiesOfClass(Player.class,
            new AABB((float) posX - 5.0F, (float) posY - 5.0F, (float) posZ - 5.0F, (float) (posX + 1) + 5.0F,
                (float) (posY + 1) + 5.0F, (float) (posZ + 1) + 5.0F))) {
            if (player.containerMenu instanceof PortableSafeContainer) {
                ContainerLevelAccess worldPos = ((PortableSafeContainer) player.containerMenu).getWorldPos();
                if (worldPos.evaluate((world, pos) -> world.getBlockEntity(pos) == tileEntity, false)) {
                    playerCount++;
                }
            }
        }

        return playerCount;
    }

    private void playSound(SoundEvent soundIn) {
        double d0 = (double) this.worldPosition.getX() + 0.5D;
        double d1 = (double) this.worldPosition.getY() + 0.5D;
        double d2 = (double) this.worldPosition.getZ() + 0.5D;

        this.level
            .playSound(null, d0, d1, d2, soundIn, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    public void openInventory(Player player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }
            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }

    }

    public void closeInventory(Player player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.onOpenOrClose();
        }
    }

    protected void onOpenOrClose() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof PortableSafeBlock) {
            this.level.blockEvent(this.worldPosition, block, 1, this.numPlayersUsing);
            this.level.updateNeighborsAt(this.worldPosition, block);
        }
    }

    public float getDoorAngle(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevDoorAngle, this.doorAngle);
    }

    public int getNumPlayersUsing() {
        return numPlayersUsing;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.invHandler != null) {
            this.invHandler.invalidate();
            this.invHandler = null;
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ITEM_HANDLER_CAPABILITY) {
            if (this.invHandler == null) {
                this.invHandler = LazyOptional.of(() -> inv);
            }
            return this.invHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public boolean isEmpty() {
        for (int i = 0; i < inv.getSlots(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public IItemHandler getInventory() {
        return inv;
    }

    @Override
    public void readData(CompoundTag compound) {
        super.readData(compound);
        if (compound.contains("Items", Tag.TAG_LIST)) {
            inv.deserializeNBT(compound);
        }
        if (compound.contains("CustomName", Tag.TAG_STRING)) {
            this.customName = Component.Serializer.fromJson(compound.getString("CustomName"));
        }
    }

    @Override
    public void writeData(CompoundTag compound) {
        writeData(compound, true);
    }

    public void writeData(CompoundTag compound, boolean includingLock) {
        if (includingLock) {
            super.writeData(compound);
        }
        compound.merge(inv.serializeNBT());
        if (this.customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    @Override
    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.customName;
    }

    protected Component getDefaultName() {
        return new TranslatableComponent("container.basedefense.portable_safe");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player playerEntity) {
        return new PortableSafeContainer(windowId, playerInv, Util.getOrDummy(level, worldPosition), inv);
    }
}
