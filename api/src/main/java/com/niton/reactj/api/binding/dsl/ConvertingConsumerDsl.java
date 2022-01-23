package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.event.EventEmitter;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A DSL node that enables converting a value to make it suitable for the consumer.
 *
 * @param <N> the type the consumer accepts
 */
public interface ConvertingConsumerDsl<N> {
    /**
     * @see ConsumerDsl#with(Supplier)
     */
    BindingDsl<N> from(Supplier<? extends N> source);

    /**
     * Applies the given function to the value returned by the source (later) and passes the result
     * to the consumer.
     *
     * @param converter the function to convert the value
     * @param <S>       the type the function will convert <b>from</b>
     */
    <S> ConvertingConsumerDsl<S> from(Function<S, ? extends N> converter);

    /**
     * @see ConsumerDsl#on(EventEmitter)
     */
    PredicatableDsl<N> from(EventEmitter<? extends N> event);
}
