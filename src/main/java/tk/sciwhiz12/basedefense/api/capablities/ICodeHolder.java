package tk.sciwhiz12.basedefense.api.capablities;

import java.util.List;

/**
 * A holder of a multiple number of {@code Long} codes.
 *
 * @author SciWhiz12
 */
public interface ICodeHolder extends IContainsCode {
    /**
     * @return A unmodifiable copy of the {@link List} of codes
     */
    List<Long> getCodes();

    /**
     * Sets the codes of this object to the given list. <br>
     *
     * @param codes A {@code List} of {@code Long} codes
     * @throws NullPointerException if the given {@code List} is null
     * @apiNote Implementors should copy the entries from the given {@link List}
     * into an internal list.
     */
    void setCodes(List<Long> codes);

    /**
     * <p>Adds the given code to this object's list.</p>
     *
     * <p>A {@code null} parameter will result in a {@link NullPointerException}.</p>
     *
     * @param code A code
     * @throws NullPointerException if the given code is {@code null}
     */
    void addCode(Long code);

    /**
     * <p>Removes the given code to this object's list. </p>
     *
     * <p>A {@code null} parameter will result in no action.</p>
     *
     * @param code A code
     */
    void removeCode(Long code);
}
