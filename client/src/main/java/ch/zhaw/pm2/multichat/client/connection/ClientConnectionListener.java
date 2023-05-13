package ch.zhaw.pm2.multichat.client.connection;

import static ch.zhaw.pm2.multichat.client.message.MessageFactory.createErrorMessage;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONNECTED;
import static ch.zhaw.pm2.multichat.protocol.payload.PayloadFactory.createErrorPayload;
import static java.util.Objects.requireNonNull;
import static javafx.application.Platform.runLater;

import ch.zhaw.pm2.multichat.client.model.ChatWindowModel;
import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.connection.ConnectionListener;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadType;
import java.util.Map;

/**
 * A class representing a listener for a client-side connection to a chat server. It extends the
 * ConnectionListener class, which is a generic class that handles incoming and outgoing payloads of
 * type Payload.
 */
public class ClientConnectionListener extends ConnectionListener<Payload> {

    private final ChatWindowModel model;

    /**
     * Constructor for the ClientConnectionListener class.
     *
     * @param connection      the connection object to listen to
     * @param payloadHandlers a map of payload types to their corresponding payload handlers
     * @param model           the chat window model
     */
    public ClientConnectionListener(final Connection<Payload> connection,
        final Map<PayloadType, PayloadHandler<Payload>> payloadHandlers,
        final ChatWindowModel model) {
        super(connection, payloadHandlers);
        this.model = requireNonNull(model);
    }

    /**
     * Returns the PayloadType of a given Payload object.
     *
     * @param payload The Payload object to get the type of.
     * @return The PayloadType of the given Payload object.
     */
    @Override
    protected PayloadType getPayloadType(Payload payload) {
        return payload.type();
    }

    /**
     * Creates an ErrorPayload object for a given error message.
     *
     * @param message The error message to create the ErrorPayload for.
     * @return The ErrorPayload object for the given error message.
     */
    @Override
    protected Payload performErrorPayloadCreation(String message) {
        return createErrorPayload(getConnection().getUsername(), message);
    }

    /**
     * Performs the disconnection sequence when a connection is lost or closed. This includes
     * clearing the messages in the ChatWindowModel, adding a disconnection error message, and
     * disconnecting the model from the connection.
     */
    @Override
    protected void performDisconnection() {
        runLater(() -> {
            if (getConnection().isState(CONNECTED)) {
                model.clearMessages();
                model.addMessage(createErrorMessage("Disconnected due to connection error"));
            }
            model.disconnect();
        });
    }

}
