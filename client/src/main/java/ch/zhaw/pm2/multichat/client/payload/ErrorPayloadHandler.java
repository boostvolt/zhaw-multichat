package ch.zhaw.pm2.multichat.client.payload;

import static ch.zhaw.pm2.multichat.client.message.MessageFactory.createErrorMessage;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONFIRM_CONNECT;
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
 * A class representing a ErrorPayloadHandler is responsible for handling error messages received
 * from the server. It implements the PayloadHandler interface and defines the handle method which
 * is called by the client when a new error message is received from the server.
 */
@Slf4j
public class ErrorPayloadHandler implements PayloadHandler<Payload> {

    private final ChatWindowModel model;

    /**
     * Constructs a new ErrorPayloadHandler with the given ChatWindowModel.
     *
     * @param model The ChatWindowModel to use for handling the error message.
     */
    public ErrorPayloadHandler(ChatWindowModel model) {
        this.model = requireNonNull(model);
    }

    /**
     * This method is called by the client when a new error message is received from the server. It
     * logs the error message, adds it to the ChatWindowModel, and updates the state of the model if
     * necessary.
     *
     * @param payload    The Payload object containing the error message received from the server.
     * @param connection The Connection object representing the connection to the server.
     */
    @Override
    public void handle(Payload payload, Connection<Payload> connection) {
        if (connection.isState(CONFIRM_CONNECT)) {
            Thread.currentThread().interrupt();
        }

        log.error(format("Received error from server: %s", payload.content()));
        runLater(() -> {
            model.addMessage(createErrorMessage(payload.content()));
            if (connection.isState(CONFIRM_CONNECT)) {
                model.setState(DISCONNECTED);
            }
        });
    }

}
