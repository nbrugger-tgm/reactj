package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.builder.exposed.ExposedReactiveBinder;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

import java.util.function.Consumer;

public class CallBuilder implements ExposedReactiveBinder {
	private final RunnableGroup runnables = new RunnableGroup();

	public RunnableCallBuilder call(Runnable runnable) {
		if (runnable == null)
			throw new IllegalArgumentException("Cannot bind a 'null' runnable");
		return new RunnableCallBuilder(runnable, this);
	}

	public <T> ConsumerBuilder<T> call(Consumer<T> runnable) {
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
