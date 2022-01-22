package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.exceptions.Exceptions;

import java.util.ServiceLoader;
import java.util.function.Consumer;

public interface BinderDsl {
	static BinderDsl create() {
		return ServiceLoader.load(BinderDsl.class)
		                    .findFirst()
		                    .orElseThrow(Exceptions.noImplementation(BinderDsl.class));
	}

	RunnableDsl call(Runnable runnable);

	<T> ConsumerDsl<T> call(Consumer<T> runnable);

}
