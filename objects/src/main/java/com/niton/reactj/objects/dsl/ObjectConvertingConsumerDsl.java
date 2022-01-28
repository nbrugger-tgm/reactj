package com.niton.reactj.objects.dsl;

import com.niton.reactj.api.binding.dsl.ConvertingConsumerDsl;
import com.niton.reactj.api.binding.dsl.PredicatableDsl;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ObjectConvertingConsumerDsl<T, O> extends ConvertingConsumerDsl<T> {
    /**
     * {@link ObjectConsumerDsl#onModelChange(Function)}
     */
    PredicatableDsl<O> onModelChange(Function<O, T> getter);

    @Override
    ObjectBindingDsl<T> from(Supplier<? extends T> source);

    @Override
    <S> ObjectConvertingConsumerDsl<S, O> from(Function<S, ? extends T> converter);

    /**
     * {@link ObjectConsumerDsl#withModel(Function)}
     */
    ObjectBindingDsl<O> fromModel(Function<O, T> converter);
}
