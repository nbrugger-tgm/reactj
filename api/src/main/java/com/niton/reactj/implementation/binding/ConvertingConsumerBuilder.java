package com.niton.reactj.implementation.binding;

import com.niton.reactj.api.binding.ConvertingConsumer;
import com.niton.reactj.api.binding.PredicateRunnable;
import com.niton.reactj.api.binding.ReactiveBinding;
import com.niton.reactj.api.binding.builder.exposed.ExposedBindingBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedConditionalEventBindingBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedSourceBindingBuilder;
import com.niton.reactj.api.event.EventEmitter;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConvertingConsumerBuilder<T, O extends CallBuilder> {
	private final O                        rootBuilder;
	private final ConvertingConsumer<T, ?> consumer;

	private static class EventBinding<T> extends ReactiveBinding<T> {
		private T event;

		public EventBinding(Consumer<T> consumer) {
			super(consumer, null);
			setSource(this::getEvent);
		}

		public T getEvent() {
			return event;
		}

		public void setEvent(T event) {
			this.event = event;
		}
	}

	public ConvertingConsumerBuilder(O rootBuilder, ConvertingConsumer<T, ?> consumer) {
		this.rootBuilder = rootBuilder;
		this.consumer = consumer;
	}

	/**
	 * use the '{@code source}' parameter to feed the consumer.
	 *
	 * @param source the source to use for calling the consumer
	 *
	 * @see ReactiveBinding
	 */
	public ExposedSourceBindingBuilder<T> from(Supplier<T> source) {
		ReactiveBinding<T> binding = new ReactiveBinding<>(consumer, source);
		return new SourceBindingBuilder<>(binding, rootBuilder);
	}

	/**
	 * Call all previous defined runnables and consumers on the occurrence of {@code event}
	 *
	 * @param event the event to subscribe to
	 */
	public ExposedBindingBuilder<T, ExposedConditionalEventBindingBuilder<T, O>> from(EventEmitter<T> event) {
		var eventBinding = new EventBinding<>(consumer);
		rootBuilder.add(eventBinding);
		PredicateRunnable<T> runnable = new PredicateRunnable<>(
				eventBinding,
				rootBuilder.getTarget()
		);
		event.listen(e -> {
			synchronized (eventBinding) {
				eventBinding.setEvent(e);
				runnable.run();
			}
		});
		return new EventBindingBuilder<>(runnable, rootBuilder);
	}


	public <F> ConvertingConsumerBuilder<F, O> from(Function<F, T> converter) {
		var newConsumer = new ConvertingConsumer<>(consumer, converter);
		return new ConvertingConsumerBuilder<>(rootBuilder, newConsumer);
	}
}
