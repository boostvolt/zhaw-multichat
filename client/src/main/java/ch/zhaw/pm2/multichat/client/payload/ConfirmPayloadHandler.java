package ch.zhaw.pm2.multichat.client.payload;

import static ch.zhaw.pm2.multichat.client.message.MessageFactory.createInfoMessage;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONFIRM_CONNECT;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONFIRM_DISCONNECT;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONNECTED;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.DISCONNECTED;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static javafx.application.Platform.runLater;

import ch.zhaw.pm2.multichat.client.model.ChatWindowModel;
import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Class representing a handler that handles confirm payloads sent by the server. The confirm
 * payload is used to confirm connection and disconnection requests. This handler updates the chat
 * window model accordingly based on the state of the connection.
 */
@Slf4j
public class ConfirmPayloadHandler implements PayloadHandler<Payload> {

    private final ChatWindowModel model;

    /**
     * Constructs a new confirm-payload handler with the given chat window model.
     *
     * @param model the chat window model to update based on the payload
     */
    public ConfirmPayloadHandler(ChatWindowModel model) {
        this.model = requireNonNull(model);
    }

    /**
     * Handles the given confirm payload by updating the chat window model accordingly based on the
     * state of the connection.
     *
     * @param payload    the confirm-payload to handle
     * @param connection the connection associated with the payload
     */
    @Override
    public void handle(Payload payload, Connection<Payload> connection) {
        runLater(() -> {
            if (connection.isState(CONFIRM_CONNECT)) {
                connection.setUsername(payload.receiver());
                model.setUsername(connection.getUsername());
                model.addMessage(createInfoMessage(payload.content()));
                model.setState(CONNECTED);
            } else if (connection.isState(CONFIRM_DISCONNECT)) {
                model.addMessage(createInfoMessage(payload.content()));
                model.setState(DISCONNECTED);
            } else {
                log.error(format("Got unexpected confirm content: %s", connection.getState()));
            }
        });
    }

}
