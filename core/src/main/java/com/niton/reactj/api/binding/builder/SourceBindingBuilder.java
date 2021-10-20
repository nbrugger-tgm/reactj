package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.PredicateRunnable;
import com.niton.reactj.api.binding.ReactiveBinding;
import com.niton.reactj.api.binding.builder.conditional.ConditionalSourceBindingBuilder;
import com.niton.reactj.api.binding.builder.exposed.BindingBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedSourceBindingCallBuilder;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

public class SourceBindingBuilder<T> extends BaseRunnableBuilder<RunnableGroup>
		implements BindingBuilder<T, ConditionalSourceBindingBuilder<T>>,
		           ExposedSourceBindingCallBuilder<T> {
	private final ReactiveBinding<T> binding;

	public SourceBindingBuilder(ReactiveBinding<T> binding, CallBuilder rootBuilder) {
		super(new RunnableGroup(binding), rootBuilder);
		this.binding = binding;
	}


	@Override
	public SourceBindingBuilder<T> and(Runnable runnable) {
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
