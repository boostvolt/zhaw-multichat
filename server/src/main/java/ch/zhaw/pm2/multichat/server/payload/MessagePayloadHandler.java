package ch.zhaw.pm2.multichat.server.payload;

import static ch.zhaw.pm2.multichat.protocol.Identifiers.EVERYONE;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONNECTED;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadFactory.createMessagePayload;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.exception.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.exception.ConnectionException;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import ch.zhaw.pm2.multichat.server.connection.ConnectionRegistry;

/**
 * A class representing MessagePayloadHandler that handles a message payload by sending it to the
 * appropriate recipient(s) or broadcasting it to all connections, depending on the contents of the
 * payload.
 */
public class MessagePayloadHandler implements PayloadHandler<Payload> {

    private final ConnectionRegistry connectionRegistry;

    /**
     * Creates a new MessagePayloadHandler instance with the specified connection registry.
     *
     * @param connectionRegistry the connection registry to be used for handling message payloads
     */
    public MessagePayloadHandler(ConnectionRegistry connectionRegistry) {
        this.connectionRegistry = requireNonNull(connectionRegistry);
    }

    /**
     * Handles the specified payload and connection. If the connection is not in the CONNECTED
     * state, a {@link ConnectionException} is thrown. If the payload is addressed to "everyone", it
     * is broadcast to all connections. If the payload is addressed to a specific recipient, it is
     * sent to that recipient and a copy is sent back to the sender.
     *
     * @param payload    the payload to be handled
     * @param connection the connection that the payload was received from
     * @throws ChatProtocolException if there is an issue with the chat protocol
     * @throws ConnectionException   if there is an issue with the connection
     */
    @Override
    public void handle(Payload payload, Connection<Payload> connection)
        throws ChatProtocolException, ConnectionException {
        if (!connection.isState(CONNECTED)) {
            throw new ConnectionException(
                format("Illegal state for message request: %s", connection.getState()));
        }

        if (EVERYONE.equalsIgnoreCase(payload.receiver())) {
            connectionRegistry.getAllConnections().forEach(c -> c.sendPayload(payload));
        } else {
            connectionRegistry.getConnection(
                payload.receiver()).sendPayload(
                createMessagePayload(connection.getUsername(), payload.receiver(),
                    payload.content()));

            connectionRegistry.getConnection(
                payload.sender()).sendPayload(
                createMessagePayload(connection.getUsername(), payload.receiver(),
                    payload.content()));
        }
    }

}
