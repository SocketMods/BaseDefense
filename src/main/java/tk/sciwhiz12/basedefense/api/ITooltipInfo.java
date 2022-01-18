package tk.sciwhiz12.basedefense.api;

import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Interface for objects that can give additional information for use in e.g. tooltips.
 *
 * @author SciWhiz12
 */
public interface ITooltipInfo {
    /**
     * <p>Adds custom information in the form of {@link Component}s to the given {@link List}.</p>
     *
     * <p>The verbose parameter specifies if the information should be verbose;
     * for items, this will be {@code true} if advanced tooltips are enabled.</p>
     *
     * @param information The list of {@code ITextComponent}s
     * @param verbose     Whether the information should be verbose
     */
    void addInformation(List<Component> information, boolean verbose);
}
