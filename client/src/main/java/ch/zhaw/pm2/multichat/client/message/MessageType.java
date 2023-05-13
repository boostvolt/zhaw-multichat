package ch.zhaw.pm2.multichat.client.message;

import static ch.zhaw.pm2.multichat.client.message.MessageCategory.LOG;
import static ch.zhaw.pm2.multichat.client.message.MessageCategory.MESSAGE;

/**
 * An enum class representing the type of messages that can be sent by the client. Each message type
 * has a corresponding {@link MessageCategory} and a method to format the message.
 */
public enum MessageType {
    /**
     * Represents a conversation message.
     */
    CONVERSATION(MESSAGE) {
        @Override
        String getFormattedMessage(Message message) {
            return String.format("[%s -> %s] %s", message.sender(), message.receiver(),
                message.content());
        }
    },

    /**
     * Represents an information log message.
     */
    INFO(LOG) {
        @Override
        String getFormattedMessage(Message message) {
            return String.format("[INFO] %s", message.content());
        }
    },

    /**
     * Represents an error log message.
     */
    ERROR(LOG) {
        @Override
        String getFormattedMessage(Message message) {
            return String.format("[ERROR] %s", message.content());
        }
    };

    private final MessageCategory category;

    /**
     * Enum constructor for the different message types in the system.
     *
     * @param category the message category of the message type
     */
    MessageType(MessageCategory category) {
        this.category = category;
    }

    /**
     * Returns the category of this message type.
     *
     * @return the category of the message type
     */
    public MessageCategory getCategory() {
        return category;
    }

    /**
     * Returns the formatted message for the given message.
     *
     * @param message the message to format
     * @return the formatted message as a String
     */
    abstract String getFormattedMessage(Message message);

}
