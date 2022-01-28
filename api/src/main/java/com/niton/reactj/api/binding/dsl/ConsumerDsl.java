package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.consumer.ConsumerGroup;
import com.niton.reactj.api.binding.consumer.ConvertingConsumer;
import com.niton.reactj.api.event.EventEmitter;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A DSL to deal with a consumer.
 *
 * @param <T> The type of the consumer.
 */
public interface ConsumerDsl<T> {
    /**
     * Execute this consumer too (with the same value)
     *
     * @see ConsumerGroup
     */
    ConsumerDsl<T> and(Consumer<? super T> consumer);

    /**
     * Execute this consumer too (with the same value)
     * <p>
     * Use this over {@link #and(Consumer)} if the consumer you want to add
     * takes a parameter whose type is a extending the type of the consumer(s) so far.
     * <br/>
     * Example: Lets assume the {@code Teacher} class extends {@code Person}.
     * <br/>
     * if you use {@code call(personConsumer)} you cannot chain {@code and(teacherConsumer)}
     * since Person != Teacher. But if you use {@code andCall(teacherConsumer)} you can chain.
     * </p>
     * <p>
     * This enables you later down the line to use predicates based on the highest type
     * (Teacher in the case before) so you can be more specific.
     * </p>
     *
     * @see ConsumerGroup
     */
    <N extends T> ConsumerDsl<N> andCall(Consumer<N> consumer);

    /**
     * Execute <b>all</b> previously defined runnables and consumers on the occurrence of {@code
     * event}
     */
    PredicatableDsl<T> on(EventEmitter<? extends T> event);

    /**
     * Use a converter to call this consumer. A source for the value to be converted will be
     * supplied via {@link ConvertingConsumerDsl#from(Supplier)} in the next step
     *
     * @param converter the function used to convert to the consumers type {@link
     *                  ConvertingConsumer}
     * @param <F>       the type to convert from
     */
    <F> ConvertingConsumerDsl<F> with(Function<F, T> converter);

    /**
     * Calls the consumer with the value casted to the needed type
     *
     * @param value the value to cast
     */
    default BindingDsl<T> withCasted(Object value) {
        return withValue((T) value);
    }

    /**
     * Call the previously defined consumer with a constant value
     *
     * @param constant the constant value to call the consumer with
     */
    BindingDsl<T> withValue(T constant);

    /**
     * Call the consumer with the casted result of the supplier
     *
     * @param getter the supplier to get the value from cast and call the consumer with
     */
    default BindingDsl<T> withCasted(Supplier<?> getter) {
        return with(() -> (T) getter.get());
    }

    /**
     * Call the consumer with the regarding return value of source.
     *
     * @param source the supplier to get the value for the consumer from
     */
    BindingDsl<T> with(Supplier<? extends T> source);
}
