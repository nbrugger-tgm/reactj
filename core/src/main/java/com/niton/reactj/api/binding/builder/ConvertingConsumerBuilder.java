package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.ReactiveBinding;
import com.niton.reactj.api.event.EventEmitter;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConvertingConsumerBuilder<T> {
	private final BindingBuilder rootBuilder;
	private final Consumer<T>    consumer;

	public ConvertingConsumerBuilder(BindingBuilder rootBuilder, Consumer<T> consumer) {
		this.rootBuilder = rootBuilder;
		this.consumer    = consumer;
	}

	/**
	 * use the '{@code source}' parameter to feed the consumer.
	 *
	 * @param source the source to use for calling the consumer
	 *
	 * @see ReactiveBinding
	 */
	public BindingCallBuilder<T> from(Supplier<T> source) {
		ReactiveBinding<T> binding = new ReactiveBinding<>(consumer, source);
		return new BindingCallBuilder<>(binding, rootBuilder);
	}

	/**
	 * Call all previous defined runnables and consumers on the occurrence of {@code event}
	 *
	 * @param event the event to subscribe to
	 */
	public void from(EventEmitter<T> event) {
		event.listen(e -> {
			consumer.accept(e);
			rootBuilder.getTarget().run();
		});
	}
}
