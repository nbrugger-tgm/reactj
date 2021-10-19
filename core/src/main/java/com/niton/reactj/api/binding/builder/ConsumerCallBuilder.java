package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.ConsumerGroup;
import com.niton.reactj.api.binding.ConvertingConsumer;
import com.niton.reactj.api.binding.ReactiveBinding;
import com.niton.reactj.api.event.EventEmitter;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConsumerCallBuilder<T> {
	private final BindingBuilder   rootBuilder;
	private final ConsumerGroup<T> consumer = new ConsumerGroup<>();

	private static class ConstantSupplier<T> implements Supplier<T> {
		private final T value;

		private ConstantSupplier(T value) {this.value = value;}

		@Override
		public T get() {
			return value;
		}
	}

	public ConsumerCallBuilder(BindingBuilder rootBuilder, Consumer<T> consumer) {
		this.rootBuilder = rootBuilder;
		this.consumer.add(consumer);
	}

	/**
	 * Execute this consumer too (with the same value)
	 *
	 * @see ConsumerGroup
	 */
	public ConsumerCallBuilder<T> and(Consumer<T> consumer) {
		this.consumer.add(consumer);
		return this;
	}

	/**
	 * Execute <b>all</b> previously defined runnables and consumers on the occurrence of {@code event}
	 */
	public void on(EventEmitter<T> event) {
		event.listen(consumer::accept);
	}

	/**
	 * Call the consumer with the regarding return value of source.
	 *
	 * @param source the supplier to get the value for the consumer from
	 */
	public BindingCallBuilder<T> with(Supplier<T> source) {
		return new BindingCallBuilder<>(new ReactiveBinding<>(consumer, source), rootBuilder);
	}

	/**
	 * Call the previously defined consumer with a constant value
	 *
	 * @param constant the constant value to call the consumer with
	 */
	public BindingCallBuilder<T> with(T constant) {
		var binding = new ReactiveBinding<>(consumer, new ConstantSupplier<>(constant));
		return new BindingCallBuilder<>(binding, rootBuilder);
	}

	/**
	 * Use a converter to call this consumer. A source for the value to be converted will be supplied via {@link
	 * ConvertingConsumerBuilder#from(Supplier)} in the next step
	 *
	 * @param converter the function used to convert to the consumers type {@link ConvertingConsumer}
	 * @param <F>       the type to convert from
	 */
	public <F> ConvertingConsumerBuilder<F> with(Function<F, T> converter) {
		var converting = new ConvertingConsumer<>(consumer, converter);
		return new ConvertingConsumerBuilder<>(rootBuilder, converting);
	}
}