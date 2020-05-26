package sciwhiz12.basedefense.api.lock;

/**
 * Represents a decision on whether to continue or suppress subsequent
 * processing.
 * 
 * @author SciWhiz12
 *
 */
public enum Decision {
    /**
     * A {@code Decision} to <strong>ALLOW and CONTINUE<strong/> subsequent
     * processing and behavior.
     */
    CONTINUE,
    /**
     * A {@code Decision} to <strong>DENY and SUPRESS<strong/> subsequent processing
     * and behaviors.
     */
    SUPPRESS;
}
