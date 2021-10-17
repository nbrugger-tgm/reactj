package com.niton.reactj.api.binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReactiveBinding<T> implements Runnable {
	protected final Consumer<T> consumer;
	protected final Supplier<T> source;

	public ReactiveBinding(Consumer<T> consumer, Supplier<T> source) {
		this.consumer = consumer;
		this.source   = source;
	}

	@Override
	public void run() {
		consumer.accept(source.get());
	}
}
