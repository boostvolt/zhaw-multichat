package ch.zhaw.pm2.multichat.protocol.payload;

import static ch.zhaw.pm2.multichat.protocol.Identifiers.SYSTEM;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.CONFIRM;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.CONNECT;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.DISCONNECT;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.ERROR;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.MESSAGE;

/**
 * A factory class for creating different types of Payload objects.
 */
public class PayloadFactory {

    private PayloadFactory() {
        // private constructor to prevent instantiation
    }

    /**
     * Creates a new Payload object of type CONNECT with the specified sender and content.
     *
     * @param sender the sender of the payload
     * @return the new Payload object
     */
    public static Payload createConnectPayload(String sender) {
        return new Payload(CONNECT, sender, SYSTEM, "");
    }

    /**
     * Creates a new Payload object of type CONFIRM with the specified receiver and content.
     *
     * @param receiver the receiver of the payload
     * @param content  the content of the payload
     * @return the new Payload object
     */
    public static Payload createConfirmPayload(String receiver, String content) {
        return new Payload(CONFIRM, SYSTEM, receiver, content);
    }

    /**
     * Creates a new Payload object of type DISCONNECT with the specified sender and content.
     *
     * @param sender the sender of the payload
     * @return the new Payload object
     */
    public static Payload createDisconnectPayload(String sender) {
        return new Payload(DISCONNECT, sender, SYSTEM, "");
    }

    /**
     * Creates a new Payload object of type MESSAGE with the specified sender, receiver, and
     * content.
     *
     * @param sender   the sender of the payload
     * @param receiver the receiver of the payload
     * @param content  the content of the payload
     * @return the new Payload object
     */
    public static Payload createMessagePayload(String sender, String receiver, String content) {
        return new Payload(MESSAGE, sender, receiver, content);
    }

    /**
     * Creates a new Payload object of type ERROR with the specified receiver and content.
     *
     * @param receiver the receiver of the payload
     * @param content  the content of the payload
     * @return the new Payload object
     */
    public static Payload createErrorPayload(String receiver, String content) {
        return new Payload(ERROR, SYSTEM, receiver, content);
    }

}
