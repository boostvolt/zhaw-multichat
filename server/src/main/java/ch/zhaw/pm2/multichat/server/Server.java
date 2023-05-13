package ch.zhaw.pm2.multichat.server;

import static ch.zhaw.pm2.multichat.protocol.NetworkHandler.DEFAULT_PORT;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadFactory.createErrorPayload;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.CONFIRM;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.CONNECT;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.DISCONNECT;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.ERROR;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadType.MESSAGE;
import static java.lang.String.format;

import ch.zhaw.pm2.multichat.protocol.NetworkHandler;
import ch.zhaw.pm2.multichat.protocol.NetworkHandler.NetworkServer;
import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.exception.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadType;
import ch.zhaw.pm2.multichat.server.connection.ConnectionRegistry;
import ch.zhaw.pm2.multichat.server.connection.ServerConnectionListener;
import ch.zhaw.pm2.multichat.server.payload.ConfirmPayloadHandler;
import ch.zhaw.pm2.multichat.server.payload.ConnectPayloadHandler;
import ch.zhaw.pm2.multichat.server.payload.DisconnectPayloadHandler;
import ch.zhaw.pm2.multichat.server.payload.ErrorPayloadHandler;
import ch.zhaw.pm2.multichat.server.payload.MessagePayloadHandler;
import java.io.IOException;
import java.net.SocketException;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

/**
 * A class representing a server that listens for incoming network connections and handles client
 * requests.
 */
@Slf4j
public class Server {

    private final ConnectionRegistry connectionRegistry = new ConnectionRegistry();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<PayloadType, PayloadHandler<Payload>> payloadHandlers = new EnumMap<>(
        PayloadType.class);
    private NetworkServer<Payload> networkServer;

    /**
     * Constructor that creates a new Server instance with the given port number.
     *
     * @param port The port number the server should listen on.
     */
    private Server(int port) {
        try {
            log.info("Create server connection...");
            networkServer = NetworkHandler.createServer(port);
            initializePayloadHandlers();
            log.info(format("Listening on <%s:%s>", networkServer.getHostAddress(),
                networkServer.getHostPort()));
        } catch (IOException e) {
            log.error(format("Could not create server on port %s", port));
        }
    }

    /**
     * Main method that creates a new Server instance and starts it with the given port number.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            final Server server = new Server(getPort(args));
            server.start();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Parses the port number from the command line arguments or returns the default value if no
     * argument is given.
     *
     * @param args The command line arguments.
     * @return The port number to listen on.
     * @throws IllegalArgumentException if the number of arguments is illegal or the argument is not
     *                                  a valid integer.
     */
    private static int getPort(String[] args) throws IllegalArgumentException {
        return switch (args.length) {
            case 0 -> DEFAULT_PORT;
            case 1 -> Integer.parseInt(args[0].trim());
            default -> throw new IllegalArgumentException(
                format("Illegal number of arguments: %s", args.length));
        };
    }

    /**
     * Initializes the payload handlers for the server.
     */
    private void initializePayloadHandlers() {
        payloadHandlers.put(CONNECT, new ConnectPayloadHandler(connectionRegistry));
        payloadHandlers.put(CONFIRM, new ConfirmPayloadHandler());
        payloadHandlers.put(DISCONNECT, new DisconnectPayloadHandler(connectionRegistry));
        payloadHandlers.put(MESSAGE, new MessagePayloadHandler(connectionRegistry));
        payloadHandlers.put(ERROR, new ErrorPayloadHandler());
    }

    /**
     * Starts the server and waits for incoming connections.
     */
    private void start() {
        log.info("Server started");
        try {
            while (!networkServer.isClosed()) {
                NetworkHandler.NetworkConnection<Payload> networkConnection = networkServer.waitForConnection();
                Connection<Payload> connection = new Connection<>(networkConnection);

                executorService.execute(
                    new ServerConnectionListener(connection, payloadHandlers, connectionRegistry));
            }
        } catch (SocketException e) {
            log.error(format("Server connection terminated: %s", e.getMessage()));
        } catch (IOException e) {
            log.error(format("Communication error: %s", e.getMessage()));
        } finally {
            terminate();
            log.info("Server terminated");
        }
    }

    /**
     * Terminates the server and closes all connections.
     */
    private void terminate() {
        try {
            connectionRegistry.getAllConnections().forEach(connection -> connection.sendPayload(
                createErrorPayload(connection.getUsername(),
                    "Disconnected due to communication error")));

            connectionRegistry.unregisterAllConnections();
            networkServer.close();
            log.info("Closed server connection");
        } catch (IOException | ChatProtocolException e) {
            log.error(format("Failed to close server connection: %s", e.getMessage()));
        }
    }

}
