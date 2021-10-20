package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.PredicateRunnable;
import com.niton.reactj.api.binding.builder.conditional.ConditionalEventBindingBuilder;
import com.niton.reactj.api.binding.builder.exposed.BindingBuilder;

public class EventBindingBuilder<T>
		implements BindingBuilder<T, ConditionalEventBindingBuilder<T>> {
	private final PredicateRunnable<T> binding;

	public EventBindingBuilder(PredicateRunnable<T> binding) {
		this.binding = binding;
	}

	@Override
	public PredicateRunnable<T> createConditionalBinding() {
		return binding;
	}


	@Override
	public ConditionalEventBindingBuilder<T> getConditionalReturn(PredicateRunnable<T> conditional) {
		return new ConditionalEventBindingBuilder<>(conditional);
	}
}
