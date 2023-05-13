package ch.zhaw.pm2.multichat.protocol;

/**
 * A class containing constants for the identifiers used in the chat protocol.
 */
public class Identifiers {

    /**
     * The system identifier for the Payload  sender.
     */
    public static final String SYSTEM = "system";

    /**
     * The identifier for a Payload receiver when it's intended for everyone.
     */
    public static final String EVERYONE = "everyone";

    /**
     * The identifier for a Payload sender when it's anonymous.
     */
    public static final String ANONYMOUS = "anonymous";

    private Identifiers() {
        // Prevent instantiation
    }

}
