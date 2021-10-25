package com.niton.reactj.api.binding.builder.conditional;

import com.niton.reactj.api.binding.builder.ConditionalBindingBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedConditionalEventBindingBuilder;
import com.niton.reactj.api.binding.predicates.HasPredicate;
import com.niton.reactj.implementation.binding.CallBuilder;

public class ConditionalEventBindingBuilder<T, O extends CallBuilder>
		implements ConditionalBindingBuilder<T, ExposedConditionalEventBindingBuilder<T, O>, O>
		, ExposedConditionalEventBindingBuilder<T, O> {
	private final HasPredicate<T> binding;
	private final O               rootBuilder;

	public ConditionalEventBindingBuilder(HasPredicate<T> binding, O rootBuilder) {
		this.binding = binding;
		this.rootBuilder = rootBuilder;
	}

	@Override
	public HasPredicate<T> getBinding() {
		return binding;
	}

	@Override
	public ExposedConditionalEventBindingBuilder<T, O> getThis() {
		return this;
	}

	@Override
	public O andAlso() {
		return rootBuilder;
	}
}
