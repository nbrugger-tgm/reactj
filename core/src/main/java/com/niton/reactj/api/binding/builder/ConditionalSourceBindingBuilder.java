package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.ConditionalBinding;
import com.niton.reactj.api.binding.ConditionalBindingWithRunnables;
import com.niton.reactj.api.binding.builder.exposed.ConditionalBindingBuilder;

public class ConditionalSourceBindingBuilder<T> extends BaseRunnableBuilder<ConditionalBindingWithRunnables<T>>
		implements ConditionalBindingBuilder<T, ConditionalSourceBindingBuilder<T>> {

	public ConditionalSourceBindingBuilder(
			ConditionalBindingWithRunnables<T> binding,
			CallBuilder rootBuilder
	) {
		super(binding, rootBuilder);
	}

	@Override
	public ConditionalBinding<T> getBinding() {
		return runnable;
	}

	@Override
	public ConditionalSourceBindingBuilder<T> getThis() {
		return this;
	}
}
