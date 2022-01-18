package tk.sciwhiz12.basedefense.capabilities;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.checkerframework.checker.nullness.qual.Nullable;
import tk.sciwhiz12.basedefense.api.ITooltipInfo;
import tk.sciwhiz12.basedefense.api.capablities.IKey;
import tk.sciwhiz12.basedefense.api.capablities.ILock;

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
    public boolean canUnlock(ILock lock, ContainerLevelAccess worldPos, @Nullable Player player) {
        return true;
    }

    @Override
    public boolean canUnlock(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {
        return key instanceof AdminKeyLock;
    }

    @Override
    public boolean canRemove(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {
        return key instanceof AdminKeyLock;
    }

    @Override
    public void onUnlock(ILock lock, ContainerLevelAccess worldPos, @Nullable Player player) {
    }

    @Override
    public void onUnlock(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {
    }

    @Override
    public void onRemove(IKey key, ContainerLevelAccess worldPos, @Nullable Player player) {
        worldPos.execute((world, pos) -> {
            world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F,
                world.random.nextFloat() * 0.1F + 0.9F);
        });
    }

    @Override
    public void addInformation(List<Component> information, boolean verbose) {
        information.add(new TranslatableComponent("tooltip.basedefense.admin_only").withStyle(ChatFormatting.DARK_RED));
    }
}
