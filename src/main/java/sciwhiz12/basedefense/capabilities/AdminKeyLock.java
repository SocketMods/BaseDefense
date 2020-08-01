package sciwhiz12.basedefense.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import sciwhiz12.basedefense.api.ITooltipInfo;
import sciwhiz12.basedefense.api.capablities.IKey;
import sciwhiz12.basedefense.api.capablities.ILock;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Lock and key capability for server administrator items.
 * <p>
 * Acts as an {@link IKey} that can open any lock.
 * Acts as an {@link ILock} that can only be opened by {@link AdminKeyLock}.
 *
 * @author SciWhiz12
 */
public class AdminKeyLock implements IKey, ILock, ITooltipInfo {

    @Override
    public boolean canUnlock(ILock lock, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        return true;
    }

    @Override
    public boolean canUnlock(IKey key, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        return key instanceof AdminKeyLock;
    }

    @Override
    public boolean canRemove(IKey key, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        return key instanceof AdminKeyLock;
    }

    @Override
    public void onUnlock(ILock lock, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player) {}

    @Override
    public void onUnlock(IKey key, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player) {}

    @Override
    public void onRemove(IKey key, @Nullable IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        if (worldPos != null) {
            worldPos.consume((world, pos) -> {
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F,
                        world.rand.nextFloat() * 0.1F + 0.9F);
            });
        }
    }

    @Override
    public void addInformation(List<ITextComponent> information, boolean verbose) {
        information.add(new TranslationTextComponent("tooltip.basedefense.admin_only").mergeStyle(TextFormatting.DARK_RED));
    }
}
