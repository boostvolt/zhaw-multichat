package ch.zhaw.pm2.multichat.protocol.connection;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import ch.zhaw.pm2.multichat.protocol.exception.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.exception.ConnectionException;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadType;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * An abstract class representing a listener for a connection, which handles incoming payloads and
 * delegates them to the appropriate payload handlers.
 *
 * @param <T> the type of payload that this connection listener can handle
 */
@Slf4j
public abstract class ConnectionListener<T extends Serializable> implements Runnable {

    private final Connection<T> connection;
    private final Map<PayloadType, PayloadHandler<T>> payloadHandlers;

    /**
     * Constructs a new connection listener with the given connection and payload handlers.
     *
     * @param connection      the connection to listen to
     * @param payloadHandlers the payload handlers to delegate incoming payloads to
     */
    protected ConnectionListener(Connection<T> connection,
        Map<PayloadType, PayloadHandler<T>> payloadHandlers) {
        this.connection = requireNonNull(connection);
        this.payloadHandlers = requireNonNull(payloadHandlers);
    }

    /**
     * Gets the connection that this listener is listening to.
     *
     * @return the connection that this listener is listening to
     */
    protected Connection<T> getConnection() {
        return connection;
    }

    /**
     * Continuously receives payloads from the connection and delegates them to the appropriate
     * payload handlers.
     */
    @Override
    public void run() {
        try {
            log.info("Start receiving data...");
            while (!Thread.currentThread().isInterrupted() && connection.getNetworkConnection()
                .isAvailable()) {
                T payload = connection.getNetworkConnection().receive();
                PayloadType payloadType = getPayloadType(payload);
                handlePayload(payload, payloadType);
            }
        } catch (ConnectionException e) {
            connection.sendPayload(
                performErrorPayloadCreation(e.getMessage()));
            performDisconnection();
        } catch (SocketException | EOFException e) {
            performDisconnection();
        } catch (IOException e) {
            log.error(format("Communication error: %s", e.getMessage()));
        } catch (ClassNotFoundException e) {
            log.error(format("Received object of unknown type: %s", e.getMessage()));
        }
        log.info("Ended Connection Listener");
    }

    private void handlePayload(T payload, PayloadType payloadType) throws ConnectionException {
        try {
            payloadHandlers.get(payloadType).handle(payload, connection);
        } catch (ChatProtocolException e) {
            connection.sendPayload(
                performErrorPayloadCreation(e.getMessage()));
        }
    }

    /**
     * Gets the type of payload for the given payload object.
     *
     * @param payload the payload object to get the type for
     * @return the type of the payload object
     */
    protected abstract PayloadType getPayloadType(T payload);

    /**
     * Performs the creation of an error payload for the given error message.
     *
     * @param message the error message to include in the error payload
     * @return the error payload object
     */
    protected abstract T performErrorPayloadCreation(String message);

    /**
     * Performs the disconnection of the connection.
     */
    protected abstract void performDisconnection();

}
