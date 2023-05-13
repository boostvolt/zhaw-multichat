package ch.zhaw.pm2.multichat.protocol.payload;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * A record representing a payload, consisting of a type, sender, receiver, and content.
 */
public record Payload(PayloadType type, String sender, String receiver, String content) implements
    Serializable {

    @Serial
    private static final long serialVersionUID = 4417145662818974403L;

    /**
     * Constructs a new Payload object with the specified type, sender, receiver, and content.
     *
     * @param type     the type of the payload
     * @param sender   the sender of the payload
     * @param receiver the receiver of the payload
     * @param content  the content of the payload
     */
    public Payload(PayloadType type, String sender, String receiver, String content) {
        this.type = requireNonNull(type);
        this.sender = requireNonNull(sender);
        this.receiver = requireNonNull(receiver);
        this.content = requireNonNull(content);
    }

}
