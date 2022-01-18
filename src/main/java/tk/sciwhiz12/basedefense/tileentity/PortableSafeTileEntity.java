package tk.sciwhiz12.basedefense.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import tk.sciwhiz12.basedefense.Reference.TileEntities;
import tk.sciwhiz12.basedefense.block.PortableSafeBlock;
import tk.sciwhiz12.basedefense.container.PortableSafeContainer;
import tk.sciwhiz12.basedefense.util.Util;

import javax.annotation.Nullable;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class PortableSafeTileEntity extends LockableTile implements ITickableTileEntity, INamedContainerProvider, INameable {
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
    private ITextComponent customName;

    public PortableSafeTileEntity() {
        super(TileEntities.PORTABLE_SAFE);
    }

    @Override
    public void tick() {
        int x = this.worldPosition.getX();
        int y = this.worldPosition.getY();
        int z = this.worldPosition.getZ();
        ++this.ticksSinceSync;
        this.numPlayersUsing = calculatePlayersUsingSync(this.level, this, this.ticksSinceSync, x, y, z,
                this.numPlayersUsing);
        this.prevDoorAngle = this.doorAngle;
        if (this.numPlayersUsing > 0 && this.doorAngle == 0.0F) {
            this.playSound(SoundEvents.CHEST_OPEN); // TODO: change to own sound
        }

        if (this.numPlayersUsing == 0 && this.doorAngle > 0.0F || this.numPlayersUsing > 0 && this.doorAngle < 1.0F) {
            float oldAngle = this.doorAngle;
            if (this.numPlayersUsing > 0) {
                this.doorAngle += 0.1F;
            } else {
                this.doorAngle -= 0.1F;
            }

            if (this.doorAngle > 1.0F) {
                this.doorAngle = 1.0F;
            }

            if (this.doorAngle < 0.5F && oldAngle >= 0.5F) {
                this.playSound(SoundEvents.CHEST_CLOSE); // TODO: change to own sound
            }

            if (this.doorAngle < 0.0F) {
                this.doorAngle = 0.0F;
            }
        }

    }

    public static int calculatePlayersUsingSync(World worldIn, TileEntity tileEntityIn, int ticksSinceSync, int posX,
            int posY, int posZ, int numPlayersUsing) {
        if (!worldIn.isClientSide && numPlayersUsing != 0 && (ticksSinceSync + posX + posY + posZ) % 200 == 0) {
            numPlayersUsing = calculatePlayersUsing(worldIn, tileEntityIn, posX, posY, posZ);
        }

        return numPlayersUsing;
    }

    public static int calculatePlayersUsing(World worldIn, TileEntity tileEntity, int posX, int posY, int posZ) {
        int playerCount = 0;

        for (PlayerEntity player : worldIn.getEntitiesOfClass(PlayerEntity.class,
                new AxisAlignedBB((float) posX - 5.0F, (float) posY - 5.0F, (float) posZ - 5.0F, (float) (posX + 1) + 5.0F,
                        (float) (posY + 1) + 5.0F, (float) (posZ + 1) + 5.0F))) {
            if (player.containerMenu instanceof PortableSafeContainer) {
                IWorldPosCallable worldPos = ((PortableSafeContainer) player.containerMenu).getWorldPos();
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
                .playSound(null, d0, d1, d2, soundIn, SoundCategory.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
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

    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }
            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }

    }

    public void closeInventory(PlayerEntity player) {
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
        return MathHelper.lerp(partialTicks, this.prevDoorAngle, this.doorAngle);
    }

    public int getNumPlayersUsing() {
        return numPlayersUsing;
    }

    @Override
    public void clearCache() {
        super.clearCache();
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
    public void setRemoved() {
        super.setRemoved();
        if (invHandler != null) {
            invHandler.invalidate();
            this.invHandler = null;
        }
    }

    @Override
    public void readData(CompoundNBT compound) {
        super.readData(compound);
        if (compound.contains("Items", Constants.NBT.TAG_LIST)) {
            inv.deserializeNBT(compound);
        }
        if (compound.contains("CustomName", Constants.NBT.TAG_STRING)) {
            this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
        }
    }

    @Override
    public CompoundNBT writeData(CompoundNBT compound) {
        return writeData(compound, true);
    }

    public CompoundNBT writeData(CompoundNBT compound, boolean includingLock) {
        if (includingLock) {
            compound = super.writeData(compound);
        }
        compound.merge(inv.serializeNBT());
        if (this.customName != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }
        return compound;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.load(getBlockState(), pkt.getTag());
    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
    }

    @Override
    public ITextComponent getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return this.customName;
    }

    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.basedefense.portable_safe");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity playerEntity) {
        return new PortableSafeContainer(windowId, playerInv, Util.getOrDummy(level, worldPosition), inv);
    }
}
