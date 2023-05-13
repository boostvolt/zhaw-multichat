package ch.zhaw.pm2.multichat.client.payload;

import static java.lang.String.format;

import ch.zhaw.pm2.multichat.protocol.connection.Connection;
import ch.zhaw.pm2.multichat.protocol.payload.Payload;
import ch.zhaw.pm2.multichat.protocol.payload.PayloadHandler;

/**
 * A class representing a PayloadHandler for handling ConnectPayloads received from the server. This
 * class throws an UnsupportedOperationException since ConnectPayload is not supported client side.
 */
public class ConnectPayloadHandler implements PayloadHandler<Payload> {

    /**
     * Throws an UnsupportedOperationException since ConnectPayload is not supported client side.
     *
     * @param payload    The payload received from the server
     * @param connection The connection handling the payload
     * @throws UnsupportedOperationException always
     */
    @Override
    public void handle(Payload payload, Connection<Payload> connection) {
        throw new UnsupportedOperationException(
            format("%s is not supported client side", getClass().getCanonicalName()));
    }

}
