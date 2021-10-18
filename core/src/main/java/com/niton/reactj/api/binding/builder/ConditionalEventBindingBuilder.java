package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.ConditionalBinding;
import com.niton.reactj.api.binding.builder.exposed.ConditionalBindingBuilder;

public class ConditionalEventBindingBuilder<T>
		implements ConditionalBindingBuilder<T, ConditionalEventBindingBuilder<T>> {
	private final ConditionalBinding<T> binding;

	public ConditionalEventBindingBuilder(ConditionalBinding<T> binding) {
		this.binding = binding;
	}

	@Override
	public ConditionalBinding<T> getBinding() {
		return binding;
	}

	@Override
	public ConditionalEventBindingBuilder<T> getThis() {
		return this;
	}
}
