package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.ConditionalBindingWithRunnables;
import com.niton.reactj.api.binding.ReactiveBinding;
import com.niton.reactj.api.binding.builder.exposed.BindingBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedSourceBindingCallBuilder;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

public class SourceBindingCallBuilder<T> extends BaseRunnableBuilder<RunnableGroup>
		implements BindingBuilder<T, ConditionalSourceBindingBuilder<T>>, ExposedSourceBindingCallBuilder<T> {
	private final ReactiveBinding<T> binding;

	public SourceBindingCallBuilder(ReactiveBinding<T> binding, CallBuilder rootBuilder) {
		super(new RunnableGroup(binding), rootBuilder);
		this.binding = binding;
	}


	@Override
	public SourceBindingCallBuilder<T> and(Runnable runnable) {
		this.runnable.add(runnable);
		return this;
	}

	@Override
	public ConditionalBindingWithRunnables<T> createConditionalBinding() {
		return new ConditionalBindingWithRunnables<>(binding, rootBuilder.getTarget());
	}

	@Override
	public ConditionalSourceBindingBuilder<T> getConditionalReturn(ConditionalBindingWithRunnables<T> conditional) {
		return new ConditionalSourceBindingBuilder<>(conditional, rootBuilder);
	}

}
