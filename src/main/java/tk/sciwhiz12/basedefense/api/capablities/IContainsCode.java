package tk.sciwhiz12.basedefense.api.capablities;

import org.checkerframework.checker.nullness.qual.Nullable;

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
    boolean containsCode(@Nullable Long code);
}
