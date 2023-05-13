package ch.zhaw.pm2.multichat.client.message;

import static java.util.Objects.requireNonNull;

/**
 * A class representing a message sent between clients in the MultiChat application. A message
 * consists of a type, sender, receiver, and content.
 */
public record Message(MessageType type, String sender, String receiver, String content) {

    /**
     * Constructs a new Message object with the given message type, sender, receiver, and content.
     *
     * @param type     the type of the message
     * @param sender   the sender of the message
     * @param receiver the receiver of the message
     * @param content  the content of the message
     */
    public Message(MessageType type, String sender, String receiver, String content) {
        this.type = requireNonNull(type);
        this.sender = requireNonNull(sender);
        this.receiver = requireNonNull(receiver);
        this.content = requireNonNull(content);
    }

    /**
     * Returns the formatted message for this object.
     *
     * @return the formatted message as a String
     */
    public String getMessage() {
        return type.getFormattedMessage(this);
    }

    /**
     * Determines if the message satisfies the given message filter.
     *
     * @param filter the message filter to apply
     * @return true if the message satisfies the filter, false otherwise
     */
    public boolean isFilterApplicable(MessageFilter filter) {
        return filter.categories().contains(type.getCategory()) && getMessage().toLowerCase()
            .contains(
                filter.content().toLowerCase());
    }

}
