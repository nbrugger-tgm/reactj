package com.niton.reactj.implementation.binding;

import com.niton.reactj.api.binding.builder.exposed.ExposedCallBuilder;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

import java.util.function.Consumer;

public class CallBuilder implements ExposedCallBuilder {
	private final RunnableGroup runnables = new RunnableGroup();

	public RunnableBuilder call(Runnable runnable) {
		if (runnable == null)
			throw new IllegalArgumentException("Cannot bind a 'null' runnable");
		return new RunnableBuilder(runnable, this);
	}

	public <T> ConsumerBuilder<T, CallBuilder> call(Consumer<T> runnable) {
		if (runnable == null)
			throw new IllegalArgumentException("Cannot bind a 'null' runnable");
		return new ConsumerBuilder<>(this, runnable);
	}

	/**
	 * Also execute {@code group} together with the previous runnable
	 */
	public void add(Runnable group) {
		runnables.add(group);
	}

	public RunnableGroup getTarget() {
		return runnables;
	}
}
