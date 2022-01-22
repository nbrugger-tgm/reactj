package com.niton.reactj.objects.dsl;

import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.objects.dsl.impl.ObjectDslImpl;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ObjectDsl<T> extends BinderDsl {

	static <T> ObjectDsl<T> create(Supplier<T> object, EventEmitter<T> emitter) {
		return new ObjectDslImpl<>(object, emitter);
	}


	<P> ObjectConsumerDsl<P, T> call(BiConsumer<T, P> setter);

	@Override
	ObjectRunnableDsl call(Runnable runnable);

	@Override
	<N> ObjectConsumerDsl<N, T> call(Consumer<N> runnable);
}
