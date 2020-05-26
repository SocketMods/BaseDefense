package sciwhiz12.basedefense.api.lock;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IKey {
    /**
     * Returns {@code true} if the unlock action was successful. <br/>
     * This does not necessarily mean that the unlock action proceeded and took
     * place, but that the key meets the necessary requirements to unlock the given
     * lock.
     * 
     * @see ILock#isUnlockAllowed(ItemStack, ItemStack, World, BlockPos, ILockable,
     *      PlayerEntity)
     * @see ILockable#isUnlockAllowed(ItemStack, ItemStack, World, BlockPos,
     *      ILockable, PlayerEntity)
     * 
     * @param lockStack The {@link ItemStack} of the {@link ILock}
     * @param keyStack  The {@code ItemStack} of the {@link IKey}
     * @param worldIn   The {@link World} where the action occurred
     * @param pos       The {@link BlockPos} where the action occurred
     * @param block     The {@link ILockable} block
     * @param player    The {@link PlayerEntity} which performed the action, or null
     *                      if no player involved
     * @return If the unlock is successful
     */
    public boolean canUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos, ILockable block,
            @Nullable PlayerEntity player);

    /**
     * Callback when the lockable is unlocked. <br/>
     * This will not be called if the decision of either {@link ILock#onUnlock} or
     * {@link ILockable#onUnlock} is {@link Decision.SUPPRESS}.
     * 
     * @param lockStack The {@link ItemStack} of the {@link ILock}
     * @param keyStack  The {@code ItemStack} of the {@link IKey}
     * @param worldIn   The {@link World} where the action occurred
     * @param pos       The {@link BlockPos} where the action occurred
     * @param block     The {@link ILockable} block
     * @param player    The {@link PlayerEntity} which performed the action, or null
     *                      if no player involved
     */
    public void onUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos, ILockable block,
            @Nullable PlayerEntity player);
}
