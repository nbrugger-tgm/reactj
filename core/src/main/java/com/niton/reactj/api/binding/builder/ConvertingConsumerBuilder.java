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

	public BindingCallBuilder<T> from(Supplier<T> source) {
		ReactiveBinding<T> binding = new ReactiveBinding<>(consumer, source);
		return new BindingCallBuilder<>(binding, rootBuilder);
	}

	public void from(EventEmitter<T> event) {
		event.listen(e -> {
			consumer.accept(e);
			rootBuilder.getTarget().run();
		});
	}
}
