package com.niton.reactj.implementation.binding;

import com.niton.reactj.api.binding.PredicateRunnable;
import com.niton.reactj.api.binding.ReactiveBinding;
import com.niton.reactj.api.binding.builder.BindingBuilder;
import com.niton.reactj.api.binding.builder.conditional.ConditionalSourceBindingBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedCallBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedSourceBindingBuilder;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

public class SourceBindingBuilder<T, O extends ExposedCallBuilder<O>> extends BaseRunnableBuilder<RunnableGroup>
		implements BindingBuilder<T, ConditionalSourceBindingBuilder<T>>,
		           ExposedSourceBindingBuilder<T> {
	private final ReactiveBinding<T> binding;

	public SourceBindingBuilder(ReactiveBinding<T> binding, O rootBuilder) {
		super(new RunnableGroup(binding), rootBuilder);
		this.binding = binding;
	}


	@Override
	public SourceBindingBuilder<T, O> and(Runnable runnable) {
		this.runnable.add(runnable);
		return this;
	}

	@Override
	public PredicateRunnable<T> createConditionalBinding() {
		return new PredicateRunnable<>(binding, runnable);
	}

	@Override
	public ConditionalSourceBindingBuilder<T> getConditionalReturn(PredicateRunnable<T> conditional) {
		return new ConditionalSourceBindingBuilder<>(conditional, rootBuilder);
	}

}
