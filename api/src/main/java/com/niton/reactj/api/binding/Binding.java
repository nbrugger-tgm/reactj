package com.niton.reactj.api.binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Binds a consumer to a supplier on {@link #run()} the value from the source is passed to the
 * consumer
 *
 * @param <T> the type of the consumer and supplier
 */
public class Binding<T> implements Runnable {
	protected final Consumer<? super T>   consumer;
	protected final Supplier<? extends T> source;

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

	public Consumer<T> getConsumer() {
		return consumer::accept;
	}

	public Supplier<T> getSource() {
		return source::get;
	}
}
