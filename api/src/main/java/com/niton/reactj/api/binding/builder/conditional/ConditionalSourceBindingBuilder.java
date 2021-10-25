package com.niton.reactj.api.binding.builder.conditional;

import com.niton.reactj.api.binding.PredicateRunnable;
import com.niton.reactj.api.binding.builder.ConditionalBindingBuilder;
import com.niton.reactj.api.binding.predicates.HasPredicate;
import com.niton.reactj.implementation.binding.BaseRunnableBuilder;
import com.niton.reactj.implementation.binding.CallBuilder;


public class ConditionalSourceBindingBuilder<T>
		extends BaseRunnableBuilder<PredicateRunnable<T>>
		implements ConditionalBindingBuilder<T, ConditionalSourceBindingBuilder<T>> {

	public ConditionalSourceBindingBuilder(
			PredicateRunnable<T> binding,
			CallBuilder rootBuilder
	) {
		super(binding, rootBuilder);
	}

	@Override
	public HasPredicate<T> getBinding() {
		return runnable;
	}

	@Override
	public ConditionalSourceBindingBuilder<T> getThis() {
		return this;
	}
}
