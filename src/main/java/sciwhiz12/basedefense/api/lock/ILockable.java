package sciwhiz12.basedefense.api.lock;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ILockable {
    /**
     * Returns {@code true} if the unlock action was allowed to proceed. <br/>
     * This does not mean if the unlock was successful, but whether the action can
     * happen due to circumstances (e.g. powered door, requiring energy to unlock)
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
     * Return value dictates whether to continue or suppress the subsequent calls to
     * {@link IKey#onUnlock}. This will not be called if the decision of
     * {@link ILock#onUnlock} is {@link Decision.SUPPRESS}.
     * 
     * @param lockStack The {@link ItemStack} of the {@link ILock}
     * @param keyStack  The {@code ItemStack} of the {@link IKey}
     * @param worldIn   The {@link World} where the action occurred
     * @param pos       The {@link BlockPos} where the action occurred
     * @param block     The {@link ILockable} block
     * @param player    The {@link PlayerEntity} which performed the action, or null
     *                      if no player involved
     * @return The decision of allowing or denying subsequent callbacks
     */
    public Decision onUnlock(ItemStack lockStack, ItemStack keyStack, World worldIn, BlockPos pos,
            ILockable block, @Nullable PlayerEntity player);

    /**
     * Returns the {@link ItemStack} that contains the {@link ILock} of this
     * instance.
     * 
     * @param world The {@link World} where the {@code ILockable} exists
     * @param pos   The {@link BlockPos} of the {@code ILockable}
     * @return the {@code ItemStack} containing this {@code ILockable}'s
     *         {@code ILock}, or {@link ItemStack.EMPTY}
     */
    public ItemStack getLock(World world, BlockPos pos);

    /**
     * Returns {@code true} if this {@code ILockable} instance contains an
     * {@link ILock}.
     * 
     * @param world The {@link World} where the {@code ILockable} exists
     * @param pos   The {@link BlockPos} of the {@code ILockable}
     * @return {@code true} if this contains an {@code ILock}, {@code false}
     *         otherwise
     */
    public boolean hasLock(World world, BlockPos pos);

    /**
     * Sets the {@link ILock} of this {@code ILockable} instance.
     * 
     * @param world The {@link World} where the {@code ILockable} exists
     * @param pos   The {@link BlockPos} of the {@code ILockable}
     * @param stack The {@link ItemStack} containing an {@code ILock}
     */
    public void setLock(World world, BlockPos pos, ItemStack stack);
}
