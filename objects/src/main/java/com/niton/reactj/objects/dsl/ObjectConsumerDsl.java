package com.niton.reactj.objects.dsl;

import com.niton.reactj.api.binding.dsl.ConsumerDsl;
import com.niton.reactj.api.binding.dsl.PredicatableDsl;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param <T> the type for the consumer
 * @param <O> the model/object type
 */
public interface ObjectConsumerDsl<T, O> extends ConsumerDsl<T> {

	@Override
	ObjectConsumerDsl<T, O> and(Consumer<? super T> consumer);

	@Override
	<N extends T> ObjectConsumerDsl<N, O> andCall(Consumer<N> consumer);

	@Override
	ObjectBindingDsl<T> withValue(T constant);

	@Override
	ObjectBindingDsl<T> with(Supplier<? extends T> source);

	@Override
	<F> ObjectConvertingConsumerDsl<F, O> with(Function<F, T> converter);

	ObjectBindingDsl<O> withModel(Function<O, T> converter);

	PredicatableDsl<O> onObjectChange(Function<O, T> getter);
}
