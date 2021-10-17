package com.niton.reactj.api.binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Binds a consumer to a supplier on {@link #run()} the value from the source is passed to the consumer
 *
 * @param <T> the type of the consumer and supplier
 */
public class ReactiveBinding<T> implements Runnable {
	protected final Consumer<T> consumer;
	protected final Supplier<T> source;

	public ReactiveBinding(Consumer<T> consumer, Supplier<T> source) {
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
}
