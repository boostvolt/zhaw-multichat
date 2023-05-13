package ch.zhaw.pm2.multichat.client.message;

import java.util.Set;

/**
 * A class representing a filter for messages that can be applied to a chat window.
 */
public record MessageFilter(Set<MessageCategory> categories, String content) {

    /**
     * A message filter that includes all categories and matches any message.
     */
    public static final MessageFilter ALL_FILTER_OPTIONS = new MessageFilter(
        Set.of(MessageCategory.values()),
        "");

}
