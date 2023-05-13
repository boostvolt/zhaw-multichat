package ch.zhaw.pm2.multichat.protocol.payload;

/**
 * An enum representing the different types of payloads.
 */
public enum PayloadType {
    /**
     * A payload type for connecting to a system.
     */
    CONNECT,

    /**
     * A payload type for confirming a connection.
     */
    CONFIRM,

    /**
     * A payload type for disconnecting from a system.
     */
    DISCONNECT,

    /**
     * A payload type for sending a message.
     */
    MESSAGE,

    /**
     * A payload type for indicating an error.
     */
    ERROR
}

