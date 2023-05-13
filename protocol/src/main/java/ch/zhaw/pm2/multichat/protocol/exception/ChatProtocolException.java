package ch.zhaw.pm2.multichat.protocol.exception;

/**
 * Checked exception class for exceptions thrown by the chat protocol.
 */
public class ChatProtocolException extends Exception {

    /**
     * Constructs a new chat protocol exception with the specified detail message.
     *
     * @param message the detail message of the exception
     */
    public ChatProtocolException(String message) {
        super(message);
    }

}
