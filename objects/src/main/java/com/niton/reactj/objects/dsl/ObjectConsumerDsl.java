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
    <F> ObjectConvertingConsumerDsl<F, O> with(Function<F, T> converter);

    @Override
    ObjectBindingDsl<T> withValue(T constant);

    @Override
    ObjectBindingDsl<T> with(Supplier<? extends T> source);

    /**
     * Call the previous consumer with the getter applied to the model.
     * @param getter the getter to apply to the model
     */
    ObjectBindingDsl<O> withModel(Function<O, T> getter);

    /**
     * Call the previous consumer with the getter applied to the model when the model changes
     * @param getter the getter to apply to the model
     */
    PredicatableDsl<O> onModelChange(Function<O, T> getter);
}
