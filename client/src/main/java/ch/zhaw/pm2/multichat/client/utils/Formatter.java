package ch.zhaw.pm2.multichat.client.utils;

import java.util.regex.Pattern;
import javafx.scene.control.TextFormatter;

/**
 * A class representing a Formatter providing a utility method that enforces a set of regular
 * expression patterns on input text.
 */
public class Formatter {

    private Formatter() {
        // private constructor to prevent instantiation
    }

    /**
     * Returns a {@code TextFormatter} that only allows changes to the input text that match at
     * least one of the specified regular expression patterns.
     *
     * @param patterns the regular expression patterns to match
     * @return a {@code TextFormatter} that enforces the specified patterns
     */
    public static TextFormatter<Void> format(Pattern... patterns) {
        return new TextFormatter<>(change -> {
            for (Pattern regex : patterns) {
                if (regex.matcher(change.getControlNewText()).matches()) {
                    return change;
                }
            }

            return null;
        });
    }

}
