package com.niton.reactj.api.binding.builder;

import java.util.function.Consumer;

public interface ExposedBindingBuilder {
	RunnableCallBuilder call(Runnable runnable);

	<T> ConsumerCallBuilder<T> call(Consumer<T> runnable);

}
