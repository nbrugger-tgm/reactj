package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.ConditionalBindingWithRunnables;
import com.niton.reactj.api.binding.builder.exposed.BindingBuilder;

public class EventBindingBuilder<T> implements BindingBuilder<T, ConditionalEventBindingBuilder<T>> {
	private final ConditionalBindingWithRunnables<T> binding;

	public EventBindingBuilder(ConditionalBindingWithRunnables<T> binding) {
		this.binding = binding;
	}

	@Override
	public ConditionalBindingWithRunnables<T> createConditionalBinding() {
		return binding;
	}


	@Override
	public ConditionalEventBindingBuilder<T> getConditionalReturn(ConditionalBindingWithRunnables<T> conditional) {
		return new ConditionalEventBindingBuilder<>(conditional);
	}
}
