package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.event.EventEmitter;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ConvertingConsumerDsl<N> {
    BindingDsl<N> from(Supplier<? extends N> source);

    <S> ConvertingConsumerDsl<S> from(Function<S, ? extends N> converter);

    PredicatableDsl<N> from(EventEmitter<? extends N> event);
}
