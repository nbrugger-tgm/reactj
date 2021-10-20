package com.niton.reactj.api.binding.builder.conditional;

import com.niton.reactj.api.binding.builder.exposed.ConditionalBindingBuilder;
import com.niton.reactj.api.binding.predicates.HasPredicate;

public class ConditionalEventBindingBuilder<T>
		implements ConditionalBindingBuilder<T, ConditionalEventBindingBuilder<T>> {
	private final HasPredicate<T> binding;

	public ConditionalEventBindingBuilder(HasPredicate<T> binding) {
		this.binding = binding;
	}

	@Override
	public HasPredicate<T> getBinding() {
		return binding;
	}

	@Override
	public ConditionalEventBindingBuilder<T> getThis() {
		return this;
	}
}
