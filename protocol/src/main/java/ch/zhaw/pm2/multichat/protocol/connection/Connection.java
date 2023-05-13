package ch.zhaw.pm2.multichat.protocol.connection;

import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.DISCONNECTED;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.NEW;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import ch.zhaw.pm2.multichat.protocol.NetworkHandler.NetworkConnection;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import lombok.extern.slf4j.Slf4j;

/**
 * The Connection class represents a connection between two peers in the network. It provides
 * methods to manage the state of the connection, send and receive data, and close the connection.
 * It is a generic class that can handle different types of serializable payloads.
 */
@Slf4j
public class Connection<T extends Serializable> {

    private final NetworkConnection<T> networkConnection;

    private ConnectionStateType state = NEW;
    private String username;

    /**
     * Creates a new Connection object with the specified NetworkConnection object. The
     * ConnectionStateType is initialized to NEW by default.
     *
     * @param networkConnection the NetworkConnection object to use for this connection
     */
    public Connection(NetworkConnection<T> networkConnection) {
        this.networkConnection = requireNonNull(networkConnection);
    }

    /**
     * Gets the network connection.
     *
     * @return the network connection
     */
    public NetworkConnection<T> getNetworkConnection() {
        return networkConnection;
    }

    /**
     * Gets the current state of the connection.
     *
     * @return the current ConnectionStateType of the connection
     */
    public ConnectionStateType getState() {
        return state;
    }

    /**
     * Sets the state of the connection.
     *
     * @param state the new ConnectionStateType to set
     */
    public void setState(ConnectionStateType state) {
        this.state = requireNonNull(state);
    }

    /**
     * Gets the username of the local peer.
     *
     * @return the username of the local peer
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the local peer.
     *
     * @param username the new username to set
     */
    public void setUsername(String username) {
        this.username = requireNonNull(username);
    }

    /**
     * Checks whether the current state of the connection matches the specified
     * ConnectionStateType.
     *
     * @param expectedState the expected ConnectionStateType
     * @return true if the current state matches the expected state, false otherwise
     */
    public boolean isState(ConnectionStateType expectedState) {
        return state == expectedState;
    }

    /**
     * Sends the provided payload if the connection is available.
     *
     * @param payload The payload to send.
     */
    public void sendPayload(T payload) {
        if (networkConnection.isAvailable()) {
            try {
                log.info(format("Sending payload: %s", payload.toString()));
                networkConnection.send(payload);
            } catch (SocketException | EOFException e) {
                log.error(format("Connection closed: %s", e.getMessage()));
                closeConnection();
            } catch (IOException e) {
                log.error(format("Communication error: %s", e.getMessage()));
                closeConnection();
            }
        }
    }

    /**
     * Stops receiving data from the network connection.
     */
    public void closeConnection() {
        log.info(format("Closing Connection Handler for %s...", username));
        try {
            setState(DISCONNECTED);
            networkConnection.close();
        } catch (IOException e) {
            log.error(format("Failed to close connection: %s", e.getMessage()));
        }
        log.info(format("Closed Connection Handler for %s", username));
    }

}
