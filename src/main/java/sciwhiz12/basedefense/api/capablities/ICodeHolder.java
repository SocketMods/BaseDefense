package sciwhiz12.basedefense.api.capablities;

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
     * Adds the given code to this object's list. <br>
     * A {@code null} parameter will result in a {@link NullPointerException}.
     *
     * @param code A code
     * @throws NullPointerException if the given code is {@code null}
     */
    void addCode(Long code);

    /**
     * Removes the given code to this object's list. <br>
     * A {@code null} parameter will result in no action.
     *
     * @param code A code
     */
    void removeCode(Long code);
}
