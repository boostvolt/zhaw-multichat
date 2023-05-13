package ch.zhaw.pm2.multichat.server.connection;

import static ch.zhaw.pm2.multichat.protocol.Identifiers.ANONYMOUS;
import static ch.zhaw.pm2.multichat.protocol.Identifiers.EVERYONE;
import static ch.zhaw.pm2.multichat.protocol.Identifiers.SYSTEM;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;

import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.exception.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.exception.ConnectionException;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a registry for managing connections to the chat server.
 */
public class ConnectionRegistry {

    private final List<Connection<Payload>> connections = new ArrayList<>();

    /**
     * Registers a new connection with the given username to the registry.
     *
     * @param connection The connection to be registered.
     * @throws ChatProtocolException If the username is already registered.
     * @throws ConnectionException   If the username is not compliant with the expected format.
     */
    public synchronized void registerConnection(Connection<Payload> connection)
        throws ChatProtocolException, ConnectionException {
        String username = connection.getUsername();

        if (isUsernameRegistered(username)) {
            throw new ChatProtocolException(format("Username %s is already registered", username));
        }

        if (!isUsernameFormatCompliant(username)) {
            throw new ConnectionException(format("Username %s is not compliant", username));
        }

        connections.add(connection);
    }

    /**
     * Unregisters the connection with the given username from the registry and closes it.
     *
     * @param username The username of the connection to be unregistered.
     */
    public synchronized void unregisterConnection(String username) {
        connections.stream()
            .filter(connection -> connection.getUsername().equalsIgnoreCase(username))
            .findFirst()
            .ifPresent(connection -> {
                connection.closeConnection();
                connections.remove(connection);
            });
    }

    /**
     * Unregisters all connections from the registry and closes them.
     */
    public synchronized void unregisterAllConnections() {
        for (Connection<Payload> connection : connections) {
            connection.closeConnection();
        }

        connections.clear();
    }

    /**
     * Retrieves the connection with the given username from the registry.
     *
     * @param username The username of the connection to be retrieved.
     * @return The connection with the given username.
     * @throws ChatProtocolException If the username is not registered.
     */
    public synchronized Connection<Payload> getConnection(String username)
        throws ChatProtocolException {
        return connections.stream()
            .filter(connection -> connection.getUsername().equalsIgnoreCase(username))
            .findFirst()
            .orElseThrow(
                () -> new ChatProtocolException(format("Username %s is not registered", username)));
    }

    /**
     * Retrieves a list of all connections registered in the registry.
     *
     * @return A list of all connections registered in the registry.
     * @throws ChatProtocolException If there are no connections registered in the registry.
     */
    public synchronized List<Connection<Payload>> getAllConnections()
        throws ChatProtocolException {
        if (connections.isEmpty()) {
            throw new ChatProtocolException("No connections registered");
        }

        return unmodifiableList(connections);
    }

    /**
     * Generates and retrieves a new anonymous username that is not yet registered in the registry.
     *
     * @return A new anonymous username.
     */
    public synchronized String getAnonymousUsername() {
        int i = 0;
        String username;
        do {
            username = format("%s%s", ANONYMOUS, i++);
        } while (isUsernameRegistered(username));

        return username;
    }

    private boolean isUsernameRegistered(String username) {
        return connections.stream()
            .anyMatch(connection -> connection.getUsername().equalsIgnoreCase(username));
    }

    private boolean isUsernameFormatCompliant(String username) {
        String lowerCaseUsername = username.toLowerCase();
        return !lowerCaseUsername.matches(".*\\s+.*") && !lowerCaseUsername.equals(
            SYSTEM.toLowerCase()) && !lowerCaseUsername.equals(EVERYONE.toLowerCase());
    }

}
