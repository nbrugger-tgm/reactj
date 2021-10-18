package com.niton.reactj.api.binding.builder.exposed;

import com.niton.reactj.api.binding.builder.ConsumerBuilder;
import com.niton.reactj.api.binding.builder.RunnableCallBuilder;

import java.util.function.Consumer;

public interface ExposedReactiveBinder {
	/**
	 * Execute this runnable when the conditions later in the chain apply
	 */
	RunnableCallBuilder call(Runnable runnable);


	/**
	 * Execute this consumer when all conditions in the chain apply
	 */
	<T> ConsumerBuilder<T> call(Consumer<T> consumer);
}
