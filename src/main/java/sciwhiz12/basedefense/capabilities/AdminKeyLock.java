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
 * <p>Lock and key capability for server administrator items.</p>
 *
 * <p>Acts as an {@link IKey} that can open any lock.<br/>
 * Acts as an {@link ILock} that can only be opened by {@link AdminKeyLock}.</p>
 *
 * @author SciWhiz12
 */
public class AdminKeyLock implements IKey, ILock, ITooltipInfo {

    @Override
    public boolean canUnlock(ILock lock, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        return true;
    }

    @Override
    public boolean canUnlock(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        return key instanceof AdminKeyLock;
    }

    @Override
    public boolean canRemove(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        return key instanceof AdminKeyLock;
    }

    @Override
    public void onUnlock(ILock lock, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {}

    @Override
    public void onUnlock(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {}

    @Override
    public void onRemove(IKey key, IWorldPosCallable worldPos, @Nullable PlayerEntity player) {
        if (worldPos != null) {
            worldPos.execute((world, pos) -> {
                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F,
                        world.random.nextFloat() * 0.1F + 0.9F);
            });
        }
    }

    @Override
    public void addInformation(List<ITextComponent> information, boolean verbose) {
        information.add(new TranslationTextComponent("tooltip.basedefense.admin_only").withStyle(TextFormatting.DARK_RED));
    }
}
