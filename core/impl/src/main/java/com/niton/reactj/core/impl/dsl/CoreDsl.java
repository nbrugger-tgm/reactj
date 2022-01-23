package com.niton.reactj.core.impl.dsl;

import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.api.binding.dsl.ConsumerDsl;
import com.niton.reactj.api.binding.dsl.RunnableDsl;

import java.util.function.Consumer;

public class CoreDsl implements BinderDsl {
	@Override
	public RunnableDsl call(Runnable runnable) {
		return new CoreRunnableDsl(runnable);
	}

	@Override
	public <T> ConsumerDsl<T> call(Consumer<T> runnable) {
		return new CoreConsumerDsl<>(runnable);
	}
}
