package ch.zhaw.pm2.multichat.protocol.payload;

import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.exception.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.exception.ConnectionException;
import java.io.Serializable;

/**
 * An interface representing a payload handler for incoming payloads.
 *
 * @param <T> the type of the payload that this handler can handle
 */
public interface PayloadHandler<T extends Serializable> {

    /**
     * Handles the given payload received through the given connection.
     *
     * @param payload    the payload to handle
     * @param connection the connection through which the payload was received
     * @throws ChatProtocolException if there is an error in the chat protocol
     * @throws ConnectionException   if there is an error in the connection
     */
    void handle(T payload, Connection<T> connection)
        throws ChatProtocolException, ConnectionException;

}
