package sciwhiz12.basedefense.api.lock;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LockContext {
    private ItemStack lock;
    private ItemStack key;
    private ILockable lockable;
    private World world;
    private BlockPos pos;
    private PlayerEntity player;

    public LockContext(ItemStack lock, ItemStack key, ILockable lockable, World world, BlockPos pos,
            PlayerEntity player) {
        this.lock = lock;
        this.key = key;
        this.lockable = lockable;
        this.world = world;
        this.pos = pos;
        this.player = player;
    }

    public ItemStack getLockItem() {
        return this.lock;
    }

    public ItemStack getKeyItem() {
        return this.key;
    }

    public ILockable getLockable() {
        return this.lockable;
    }

    public World getWorld() {
        return this.world;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }
}
