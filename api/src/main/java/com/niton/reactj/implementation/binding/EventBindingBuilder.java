package com.niton.reactj.implementation.binding;

import com.niton.reactj.api.binding.PredicateRunnable;
import com.niton.reactj.api.binding.builder.BindingBuilder;
import com.niton.reactj.api.binding.builder.conditional.ConditionalEventBindingBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedConditionalEventBindingBuilder;

public class EventBindingBuilder<T, O extends CallBuilder>
		implements BindingBuilder<T, ExposedConditionalEventBindingBuilder<T, O>> {
	private final PredicateRunnable<T> binding;
	private final O                    rootBuilder;

	public EventBindingBuilder(PredicateRunnable<T> binding, O rootBuilder) {
		this.binding = binding;
		this.rootBuilder = rootBuilder;
	}

	@Override
	public PredicateRunnable<T> createConditionalBinding() {
		return binding;
	}


	@Override
	public ExposedConditionalEventBindingBuilder<T, O> getConditionalReturn(PredicateRunnable<T> conditional) {
		return new ConditionalEventBindingBuilder<>(conditional, rootBuilder);
	}
}
