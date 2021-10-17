package com.niton.reactj.api.binding.builder;

import java.util.function.Consumer;

public interface ExposedBindingBuilder {
	/**
	 * Execute this runnable when the conditions later in the chain apply
	 */
	RunnableCallBuilder call(Runnable runnable);


	/**
	 * Execute this consumer when all conditions in the chain apply
	 */
	<T> ConsumerCallBuilder<T> call(Consumer<T> consumer);

}
