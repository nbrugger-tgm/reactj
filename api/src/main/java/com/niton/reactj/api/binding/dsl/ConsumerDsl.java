package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.ConsumerGroup;
import com.niton.reactj.api.binding.ConvertingConsumer;
import com.niton.reactj.api.event.EventEmitter;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ConsumerDsl<T> {
	interface SuperConsumer<N> extends Consumer<N> {
	}

	/**
	 * Execute this consumer too (with the same value)
	 *
	 * @see ConsumerGroup
	 */
	ConsumerDsl<T> and(Consumer<? super T> consumer);

	/**
	 * Execute this consumer too (with the same value)
	 *
	 * @see ConsumerGroup
	 */
	<N extends T> ConsumerDsl<N> andCall(Consumer<N> consumer);


	/**
	 * Execute <b>all</b> previously defined runnables and consumers on the occurrence of {@code
	 * event}
	 *
	 * @return
	 */
	PredicatableDsl<T> on(EventEmitter<? extends T> event);

	/**
	 * Use a converter to call this consumer. A source for the value to be converted will be
	 * supplied via {@link
	 * ConvertingConsumerBuilder#from(Supplier)} in the next step
	 *
	 * @param converter the function used to convert to the consumers type {@link
	 *                  ConvertingConsumer}
	 * @param <F>       the type to convert from
	 */
	<F> ConvertingConsumerDsl<F> with(Function<F, T> converter);

	default BindingDsl<T> withCasted(Object value) {
		return withValue((T) value);
	}

	/**
	 * Call the previously defined consumer with a constant value
	 *
	 * @param constant the constant value to call the consumer with
	 */
	BindingDsl<T> withValue(T constant);

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
