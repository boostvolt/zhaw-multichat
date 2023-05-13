package ch.zhaw.pm2.multichat.server.payload;

import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONNECTED;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.DISCONNECTED;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadFactory.createConfirmPayload;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.exception.ConnectionException;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import ch.zhaw.pm2.multichat.server.connection.ConnectionRegistry;

/**
 * A class representing a DisconnectPayloadHandler that handles a disconnect payload and unregisters
 * the connection from the {@link ConnectionRegistry}. If the connection is already in the
 * DISCONNECTED state, a {@link ConnectionException} is thrown. A confirm-payload is created and
 * sent to the connection, and the connection is unregistered from the ConnectionRegistry if it was
 * previously registered.
 */
public class DisconnectPayloadHandler implements PayloadHandler<Payload> {

    private final ConnectionRegistry connectionRegistry;

    /**
     * Creates a new DisconnectPayloadHandler instance with the specified connection registry.
     *
     * @param connectionRegistry the connection registry to be used for unregistering connections
     */
    public DisconnectPayloadHandler(ConnectionRegistry connectionRegistry) {
        this.connectionRegistry = requireNonNull(connectionRegistry);
    }

    /**
     * Handles the specified payload and connection. If the connection is already in the
     * DISCONNECTED state, a {@link ConnectionException} is thrown. A confirm payload is created and
     * sent to the connection, and the connection is unregistered from the ConnectionRegistry if it
     * was previously registered.
     *
     * @param payload    the payload to be handled
     * @param connection the connection that the payload was received from
     * @throws ConnectionException if there is an issue with the connection
     */
    @Override
    public void handle(Payload payload, Connection<Payload> connection)
        throws ConnectionException {
        String username = connection.getUsername();

        if (connection.isState(DISCONNECTED)) {
            throw new ConnectionException(
                format("Illegal state for disconnect request: %s", connection.getState()));
        }

        connection.sendPayload(
            createConfirmPayload(username, format("Confirm disconnect of %s", username)));

        if (connection.isState(CONNECTED)) {
            connectionRegistry.unregisterConnection(username);
        }
    }

}
