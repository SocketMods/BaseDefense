package tk.sciwhiz12.basedefense.api.capablities;

/**
 * An object which may contain a {@link Long} code or codes.
 *
 * @author SciWhiz12
 */
public interface IContainsCode {
    /**
     * @param code The {@code Long} code to be checked
     * @return {@code true} if this contains the given code, otherwise {@code false}
     */
    boolean containsCode(Long code);
}
