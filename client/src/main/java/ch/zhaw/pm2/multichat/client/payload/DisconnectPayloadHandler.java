package ch.zhaw.pm2.multichat.client.payload;

import static ch.zhaw.pm2.multichat.client.message.MessageFactory.createInfoMessage;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.DISCONNECTED;
import static java.util.Objects.requireNonNull;
import static javafx.application.Platform.runLater;

import ch.zhaw.pm2.multichat.client.model.ChatWindowModel;
import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * A class representing DisconnectPayload Handler that is responsible for handling payloads that
 * instruct the client to disconnect from the server. When the handler receives a disconnect
 * payload, it closes the connection, sets the client's state to disconnected, and adds an info
 * message to the chat window model indicating that the user has disconnected.
 */
@Slf4j
public class DisconnectPayloadHandler implements PayloadHandler<Payload> {

    private final ChatWindowModel model;

    public DisconnectPayloadHandler(ChatWindowModel model) {
        this.model = requireNonNull(model);
    }

    /**
     * Handles a disconnect payload received from the server. Closes the connection and sets the
     * client's state to disconnected. Adds an info message to the chat window model indicating that
     * the user has disconnected.
     *
     * @param payload    the disconnect payload to handle
     * @param connection the connection to the server
     */
    @Override
    public void handle(Payload payload, Connection<Payload> connection) {
        runLater(() -> {
            if (connection.isState(DISCONNECTED)) {
                log.info("Connection is already disconnected");
                return;
            }

            connection.closeConnection();
            model.setState(DISCONNECTED);
            model.addMessage(createInfoMessage(payload.content()));
        });
    }

}
