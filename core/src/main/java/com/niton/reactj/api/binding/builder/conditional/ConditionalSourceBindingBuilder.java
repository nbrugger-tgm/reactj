package com.niton.reactj.api.binding.builder.conditional;

import com.niton.reactj.api.binding.PredicateRunnable;
import com.niton.reactj.api.binding.builder.BaseRunnableBuilder;
import com.niton.reactj.api.binding.builder.CallBuilder;
import com.niton.reactj.api.binding.builder.exposed.ConditionalBindingBuilder;
import com.niton.reactj.api.binding.predicates.HasPredicate;


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
