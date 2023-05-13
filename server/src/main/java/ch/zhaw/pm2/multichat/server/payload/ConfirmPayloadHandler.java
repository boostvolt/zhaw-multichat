package ch.zhaw.pm2.multichat.server.payload;

import static java.lang.String.format;

import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;

/**
 * A class representing a ConfirmPayloadHandler that is a server-side implementation of the
 * PayloadHandler interface that handles ConfirmPayloads.
 */
public class ConfirmPayloadHandler implements PayloadHandler<Payload> {

    /**
     * Throws an exception since the ConfirmPayload is not supported on server side.
     *
     * @param payload    the payload to handle
     * @param connection the connection to handle the payload on
     * @throws UnsupportedOperationException this method is not supported server-side
     */
    @Override
    public void handle(Payload payload, Connection<Payload> connection) {
        throw new UnsupportedOperationException(
            format("%s is not supported server side", getClass().getCanonicalName()));
    }

}
