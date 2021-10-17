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

	public ConsumerCallBuilder<T> and(Consumer<T> consumer) {
		this.consumer.add(consumer);
		return this;
	}

	public void on(EventEmitter<T> event) {
		event.listen(consumer::accept);
	}

	public BindingCallBuilder<T> with(Supplier<T> source) {
		return new BindingCallBuilder<>(new ReactiveBinding<>(consumer, source), rootBuilder);
	}

	public BindingCallBuilder<T> with(T value) {
		var binding = new ReactiveBinding<>(consumer, new ConstantSupplier<>(value));
		return new BindingCallBuilder<>(binding, rootBuilder);
	}

	public <F> ConvertingConsumerBuilder<F> with(Function<F, T> converter) {
		var converting = new ConvertingConsumer<>(consumer, converter);
		return new ConvertingConsumerBuilder<>(rootBuilder, converting);
	}
}
