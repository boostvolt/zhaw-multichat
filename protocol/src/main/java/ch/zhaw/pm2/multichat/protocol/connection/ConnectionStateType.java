package ch.zhaw.pm2.multichat.protocol.connection;

/**
 * An enumeration representing the different states of a chat connection.
 */
public enum ConnectionStateType {
    /**
     * A new connection state.
     */
    NEW,

    /**
     * A connection state indicating that the connection has been confirmed.
     */
    CONFIRM_CONNECT,

    /**
     * A connection state indicating that the connection is established.
     */
    CONNECTED,

    /**
     * A connection state indicating that the disconnect has been confirmed.
     */
    CONFIRM_DISCONNECT,

    /**
     * A connection state indicating that the connection has been terminated.
     */
    DISCONNECTED
}
