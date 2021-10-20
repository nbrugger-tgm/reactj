package com.niton.reactj.api.binding.builder;

import java.util.function.Supplier;

public class ReactiveBinder<T extends CallBuilder> {
	private final Supplier<T> builderCreator;

	public ReactiveBinder(Supplier<T> builderCreator) {this.builderCreator = builderCreator;}

	public T newBinding() {
		return builderCreator.get();
	}
}
