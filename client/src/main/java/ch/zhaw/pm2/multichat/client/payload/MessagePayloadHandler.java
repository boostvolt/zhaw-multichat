package ch.zhaw.pm2.multichat.client.payload;

import static ch.zhaw.pm2.multichat.client.message.MessageFactory.createConversationMessage;
import static ch.zhaw.pm2.multichat.protocol.connection.ConnectionStateType.CONNECTED;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static javafx.application.Platform.runLater;

import ch.zhaw.pm2.multichat.client.model.ChatWindowModel;
import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * A class representing a PayloadHandler implementation that handles incoming message payloads from
 * the server. This handler adds incoming messages to the associated ChatWindowModel.
 */
@Slf4j
public class MessagePayloadHandler implements PayloadHandler<Payload> {

    private final ChatWindowModel model;

    /**
     * Constructs a new MessagePayloadHandler with the given ChatWindowModel.
     *
     * @param model The ChatWindowModel associated with this MessagePayloadHandler
     */
    public MessagePayloadHandler(ChatWindowModel model) {
        this.model = requireNonNull(model);
    }

    /**
     * Handles the incoming message payload by adding the message to the associated ChatWindowModel.
     * If the connection is not in the CONNECTED state, logs an error and returns.
     *
     * @param payload    The incoming message payload
     * @param connection The connection associated with this payload
     */
    @Override
    public void handle(Payload payload, Connection<Payload> connection) {
        runLater(() -> {
            if (!connection.isState(CONNECTED)) {
                log.info(
                    format("Illegal state %s for content: %s", connection.getState(),
                        payload.content()));
                return;
            }

            model.addMessage(
                createConversationMessage(payload.sender(), payload.receiver(), payload.content()));
        });
    }

}
