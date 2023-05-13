package ch.zhaw.pm2.multichat.server.payload;

import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * A class representing an ErrorPayloadHandler that handles an error payload by logging the error
 * message and the associated connection.
 */
@Slf4j
public class ErrorPayloadHandler implements PayloadHandler<Payload> {

    /**
     * Handles the specified payload and connection by logging the error message and the associated
     * connection.
     *
     * @param payload    the payload to be handled
     * @param connection the connection that the payload was received from
     */
    @Override
    public void handle(Payload payload, Connection<Payload> connection) {
        log.error(
            String.format("Received error from connection: %s with message: %s", connection,
                payload.content()));
    }

}
