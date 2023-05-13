package ch.zhaw.pm2.multichat.client.utils;

import static ch.zhaw.pm2.multichat.protocol.Identifiers.EVERYONE;
import static java.lang.String.format;

import ch.zhaw.pm2.multichat.protocol.exception.ChatProtocolException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class representing MessageParser used for parsing user input into message, receiver and
 * content.
 */

public class MessageParser {

    /**
     * Regular expression pattern to parse user input into a content and recipient.
     */
    private static final Pattern messagePattern = Pattern.compile("^(?:@(\\S*))?\\s*(.*)$");

    /**
     * Regular expression pattern to parse user input into a content and recipient.
     */
    private MessageParser() {
        // private constructor to prevent instantiation
    }

    /**
     * Parses the given message into a {@link MessageParserResult}, which contains the message
     * receiver and content.
     *
     * @param message the message to parse
     * @return the parsed {@link MessageParserResult}
     * @throws ChatProtocolException if the message could not be parsed
     */
    public static MessageParserResult parse(String message) throws ChatProtocolException {
        Matcher matcher = messagePattern.matcher(message);

        if (matcher.find()) {
            String receiver = matcher.group(1);
            String content = matcher.group(2);
            if (receiver == null || receiver.isBlank()) {
                receiver = EVERYONE;
            }

            return new MessageParserResult(receiver, content);
        } else {
            throw new ChatProtocolException(format("Couldn't parse message: %s", message));
        }
    }

    /**
     * A record to hold the message receiver and content after parsing.
     */
    public record MessageParserResult(String receiver, String message) {

    }

}
