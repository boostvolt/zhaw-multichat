package ch.zhaw.pm2.multichat.server.payload;

import static ch.zhaw.pm2.multichat.protocol.Identifiers.ANONYMOUS;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONNECTED;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.NEW;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadFactory.createConfirmPayload;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.exception.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.exception.ConnectionException;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import ch.zhaw.pm2.multichat.server.connection.ConnectionRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * A class representing a ConnectPayloadHandler that handles a connect payload and registers a new
 * connection to the {@link ConnectionRegistry}. If the connection is not in the NEW state, a
 * {@link ConnectionException} is thrown. The sender's username is set to the connection's username,
 * or an anonymous username is generated. if the sender is anonymous. The connection is then
 * registered with the ConnectionRegistry. Finally, a confirm-payload is created and sent to the
 * connection.
 */
@Slf4j
public class ConnectPayloadHandler implements PayloadHandler<Payload> {

    private final ConnectionRegistry connectionRegistry;

    /**
     * Creates a new ConnectPayloadHandler instance with the specified connection registry.
     *
     * @param connectionRegistry the connection registry to be used for registering new connections
     */
    public ConnectPayloadHandler(ConnectionRegistry connectionRegistry) {
        this.connectionRegistry = requireNonNull(connectionRegistry);
    }

    /**
     * Handles the specified payload and connection. If the connection is not in the NEW state, a
     * {@link ConnectionException} is thrown. The sender's username is set to the connection's
     * username, or an anonymous username is generated if the sender is anonymous. The connection is
     * then registered with the ConnectionRegistry. Finally, a confirm-payload is created and sent
     * to the connection.
     *
     * @param payload    the payload to be handled
     * @param connection the connection that the payload was received from
     * @throws ChatProtocolException if there is an issue with the chat protocol
     * @throws ConnectionException   if there is an issue with the connection
     */
    @Override
    public void handle(Payload payload, Connection<Payload> connection)
        throws ChatProtocolException, ConnectionException {
        if (!connection.isState(NEW)) {
            throw new ConnectionException(
                format("Illegal state for connect request: %s", connection.getState()));
        }

        if (!ANONYMOUS.equalsIgnoreCase(payload.sender())) {
            connection.setUsername(payload.sender());
        } else {
            connection.setUsername(connectionRegistry.getAnonymousUsername());
        }

        connectionRegistry.registerConnection(connection);

        connection.sendPayload(createConfirmPayload(connection.getUsername(),
            format("Registration successful for %s", connection.getUsername())));
        connection.setState(CONNECTED);
    }

}
