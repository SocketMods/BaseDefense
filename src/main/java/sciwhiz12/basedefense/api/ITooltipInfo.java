package sciwhiz12.basedefense.api;

import net.minecraft.util.text.ITextComponent;

import java.util.List;

/**
 * Interface for objects that can give additional information for use in e.g. tooltips.
 *
 * @author SciWhiz12
 */
public interface ITooltipInfo {
    /**
     * Adds custom information in the form of {@link ITextComponent}s to the given {@link List}.
     *
     * The verbose parameter specifies if the information should be verbose;
     * for items, this will be {@code true} if advanced tooltips are enabled.
     *
     * @param information The list of {@code ITextComponent}s
     * @param verbose Whether the information should be verbose
     */
    void addInformation(List<ITextComponent> information, boolean verbose);
}
