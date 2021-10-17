package com.niton.reactj.api.binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReactiveBinding<T> implements Runnable {
	protected final Consumer<T> sink;
	protected final Supplier<T> source;

	public ReactiveBinding(Consumer<T> sink, Supplier<T> source) {
		this.sink   = sink;
		this.source = source;
	}

	@Override
	public void run() {
		sink.accept(source.get());
	}
}
