package com.niton.reactj.api.binding;

import com.niton.reactj.api.binding.runnable.RunnableGroup;

public class ConditionalBindingWithRunnables<T> extends ConditionalBinding<T> {
	private final RunnableGroup runnables;

	public ConditionalBindingWithRunnables(
			ReactiveBinding<T> binding,
			RunnableGroup runnables
	) {
		super(binding);
		this.runnables = runnables;
	}

	@Override
	public void run() {
		T val = binding.source.get();
		if (getPredicate().test(val)) {
			binding.consumer.accept(val);
			runnables.run();
		}
	}
}
