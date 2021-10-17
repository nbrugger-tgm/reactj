package com.niton.reactj.api.binding;

import java.util.function.Consumer;
import java.util.function.Function;

public class ConvertingConsumer<F, T> implements Consumer<F> {
	private final Consumer<T>    target;
	private final Function<F, T> converter;

	public ConvertingConsumer(Consumer<T> target, Function<F, T> converter) {
		this.target    = target;
		this.converter = converter;
	}

	@Override
	public void accept(F t) {
		target.accept(converter.apply(t));
	}
}
