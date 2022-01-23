package com.niton.reactj.api.binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Binds a consumer to a supplier on {@link #run()} the value from the source is passed to the
 * consumer.
 *
 * @param <T> the type of the consumer and supplier
 */
public class Binding<T> implements Runnable {
    /**
     * The consumer that should be called when {@link #run()} is called.
     */
    protected final Consumer<? super T>   consumer;
    /**
     * The supplier that should be used to call {@link #consumer} when {@link #run()} is called.
     */
    protected final Supplier<? extends T> source;

    /**
     * @param consumer the consumer to feed values from the source to
     * @param source   the source to get values from
     */
    public Binding(Consumer<? super T> consumer, Supplier<? extends T> source) {
        if (consumer == null)
            throw new IllegalArgumentException("Can't bind null consumer");
        this.consumer = consumer;
        this.source   = source;
    }

    /**
     * passes the value from {@link #source} on to {@link #consumer}
     */
    @Override
    public void run() {
        consumer.accept(source.get());
    }

    /**
     * @return a Consumer that forwards to the consumer of this binding
     */
    public Consumer<T> getConsumer() {
        return consumer::accept;
    }

    /**
     * @return a Supplier that forwards from the source of this binding
     */
    public Supplier<T> getSource() {
        return source::get;
    }
}
