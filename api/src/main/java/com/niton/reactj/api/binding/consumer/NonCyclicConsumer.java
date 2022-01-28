package com.niton.reactj.api.binding.consumer;

import java.util.function.Consumer;

/**
 * A NonCyclicConsumer is a Consumer that can be used to consume a value without causing a cycle.
 * {@link com.niton.reactj.api.binding.runnable.NonCyclicRunnable}
 * @param <T> {@link Consumer}
 */
public class NonCyclicConsumer<T> implements Consumer<T> {
    /**
     * The consumer to actually use
     */
    private final Consumer<? super T> consumer;
    /**
     * If true the consumer is currently (already) consuming
     */
    private boolean consuming = false;
    public NonCyclicConsumer(Consumer<? super T> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void accept(T t) {
        if(consuming)
            return;
        consuming = true;
        consumer.accept(t);
        consuming = false;
    }
}
