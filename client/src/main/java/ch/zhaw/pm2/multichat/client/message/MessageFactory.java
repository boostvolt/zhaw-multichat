package ch.zhaw.pm2.multichat.client.message;

import static ch.zhaw.pm2.multichat.client.message.MessageType.CONVERSATION;
import static ch.zhaw.pm2.multichat.client.message.MessageType.ERROR;
import static ch.zhaw.pm2.multichat.client.message.MessageType.INFO;
import static ch.zhaw.pm2.multichat.protocol.Identifiers.SYSTEM;

/**
 * A utility class representing a factory for {@link Message} objects.
 */
public class MessageFactory {


    private MessageFactory() {
        // private constructor to prevent instantiation
    }

    /**
     * Creates a new {@link Message} object of type {@link MessageType#CONVERSATION}.
     *
     * @param sender   the sender of the message
     * @param receiver the receiver of the message
     * @param message  the content of the message
     * @return a new {@link Message} object
     */
    public static Message createConversationMessage(String sender, String receiver,
        String message) {
        return new Message(CONVERSATION, sender, receiver, message);
    }

    /**
     * Creates a new {@link Message} object of type {@link MessageType#INFO}.
     *
     * @param message the content of the message
     * @return a new {@link Message} object
     */
    public static Message createInfoMessage(String message) {
        return new Message(INFO, SYSTEM, SYSTEM, message);
    }

    /**
     * Creates a new {@link Message} object of type {@link MessageType#ERROR}.
     *
     * @param message the content of the message
     * @return a new {@link Message} object
     */
    public static Message createErrorMessage(String message) {
        return new Message(ERROR, SYSTEM, SYSTEM, message);
    }

}
