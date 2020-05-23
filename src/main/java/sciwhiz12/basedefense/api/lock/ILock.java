package sciwhiz12.basedefense.api.lock;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ILock {
    /**
     * Returns {@code true} if the unlock action was allowed to proceed. <br/>
     * This does not mean if the unlock was successful (that is determined by
     * {@link IKey#canUnlock}), but whether the action can happen due to
     * circumstances (e.g. powered door, requiring energy to unlock)
     * 
     * @see IKey#canUnlock(ItemStack, ItemStack, World, BlockPos, ILockable,
     *      PlayerEntity)
     * 
     * @param lockStack The {@link ItemStack} of the {@link ILock}
     * @param keyStack  The {@code ItemStack} of the {@link IKey}
     * @param worldIn   The {@link World} where the action occurred
     * @param pos       The {@link BlockPos} where the action occurred
     * @param block     The {@link ILockable} block
     * @param player    The {@link PlayerEntity} which performed the action, or null
     *                      if no player involved
     * @return {@code true} if the unlock is allowed, {@code false} otherwise
     */
    public boolean isUnlockAllowed(ItemStack lockStack, ItemStack keyStack, World worldIn,
            BlockPos pos, ILockable block, @Nullable PlayerEntity player);

    /**
     * Callback when the lockable is unlocked. <br/>
     * Return value dictates whether to continue or suppress the subsequent call to
     * {@link ILockable#onUnlock} and {@link IKey#onUnlock}. <br/>
     * 
     * @param lockStack The {@link ItemStack} of the {@link ILock}
     * @param keyStack  The {@code ItemStack} of the {@link IKey}
     * @param worldIn   The {@link World} where the action occurred
     * @param pos       The {@link BlockPos} where the action occurred
     * @param block     The {@link ILockable} block
     * @param player    The {@link PlayerEntity} which performed the action, or null
     *                      if no player involved
     * @return The decision of allowing or denying subsequent callback
     */
    public Decision onUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos,
            ILockable block, @Nullable PlayerEntity player);
}
