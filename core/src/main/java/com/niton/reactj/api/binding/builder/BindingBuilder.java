package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.runnable.RunnableGroup;

import java.util.function.Consumer;

public class BindingBuilder implements ExposedBindingBuilder {
	private final RunnableGroup runnables = new RunnableGroup();

	public RunnableCallBuilder call(Runnable runnable) {
		if (runnable == null)
			throw new IllegalArgumentException("Cannot bind a 'null' runnable");
		return new RunnableCallBuilder(runnable, this);
	}

	public <T> ConsumerCallBuilder<T> call(Consumer<T> runnable) {
		if (runnable == null)
			throw new IllegalArgumentException("Cannot bind a 'null' runnable");
		return new ConsumerCallBuilder<>(this, runnable);
	}

	public void add(Runnable group) {
		runnables.add(group);
	}

	public Runnable getTarget() {
		return runnables;
	}
}
