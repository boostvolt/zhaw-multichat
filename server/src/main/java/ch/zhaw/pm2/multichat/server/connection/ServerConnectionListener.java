package ch.zhaw.pm2.multichat.server.connection;

import static ch.zhaw.pm2.multichat.protocol.payload.PayloadFactory.createDisconnectPayload;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadFactory.createErrorPayload;
import static java.util.Objects.requireNonNull;

import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.connection.ConnectionListener;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadType;
import java.util.Map;

/**
 * A class representing a listener for the server connection. It extends the ConnectionListener
 * class and adds functionality to perform error payload creation and disconnection. It also holds a
 * reference to the ConnectionRegistry for registering and unregistering connections.
 */
public class ServerConnectionListener extends ConnectionListener<Payload> {

    private final ConnectionRegistry connectionRegistry;

    /**
     * Constructs a new ServerConnectionListener with the given Connection, payloadHandlers and
     * ConnectionRegistry.
     *
     * @param connection         the Connection to listen to
     * @param payloadHandlers    the payloadHandlers to use for processing payloads
     * @param connectionRegistry the ConnectionRegistry for registering and unregistering
     *                           connections
     */
    public ServerConnectionListener(Connection<Payload> connection,
        Map<PayloadType, PayloadHandler<Payload>> payloadHandlers,
        ConnectionRegistry connectionRegistry) {
        super(connection, payloadHandlers);
        this.connectionRegistry = requireNonNull(connectionRegistry);
    }

    /**
     * Returns the payload type of the given payload.
     *
     * @param payload the payload to get the type from
     * @return the payload type of the given payload
     */
    @Override
    protected PayloadType getPayloadType(Payload payload) {
        return payload.type();
    }

    /**
     * Performs the creation of an error payload with the given message.
     *
     * @param message the message to create the error payload with
     * @return the error payload with the given message
     */
    @Override
    protected Payload performErrorPayloadCreation(String message) {
        return createErrorPayload(getConnection().getUsername(), message);
    }

    /**
     * Performs the disconnection of the current connection. Sends a disconnect payload to the
     * client and unregisters the connection from the ConnectionRegistry.
     */
    @Override
    protected void performDisconnection() {
        String username = getConnection().getUsername();

        getConnection().sendPayload(createDisconnectPayload(username));
        connectionRegistry.unregisterConnection(username);
    }

}
