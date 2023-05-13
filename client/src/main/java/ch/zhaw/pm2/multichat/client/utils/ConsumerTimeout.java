package ch.zhaw.pm2.multichat.client.utils;

import java.util.function.Consumer;

/**
 * A class representing wrapper for a {@link Consumer} that imposes a minimum delay between
 * invocations This ensures that the consumer is not called too frequently.
 *
 * @param <T> the type of the input to the consumer
 */
public class ConsumerTimeout<T> implements Consumer<T> {

    private final Consumer<T> consumer;
    private final long timeout;
    private long lastCall;

    /**
     * Constructs a new {@code ConsumerTimeout} object.
     *
     * @param consumer the consumer to be wrapped
     * @param timeout  the minimum time between invocations, in milliseconds
     */
    public ConsumerTimeout(Consumer<T> consumer, long timeout) {
        this.consumer = consumer;
        this.timeout = timeout;
        this.lastCall = System.currentTimeMillis();
    }

    /**
     * Accepts the input argument, invoking the wrapped consumer only if the minimum time between
     * invocations has elapsed since the last call.
     *
     * @param t the input argument
     */
    @Override
    public void accept(T t) {
        long now = System.currentTimeMillis();
        if (now - lastCall > timeout) {
            consumer.accept(t);
            lastCall = now;
        }
    }

}
