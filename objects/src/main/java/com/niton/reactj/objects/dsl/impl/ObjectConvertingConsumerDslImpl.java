package com.niton.reactj.objects.dsl.impl;

import com.niton.reactj.api.binding.dsl.ConvertingConsumerDsl;
import com.niton.reactj.api.binding.dsl.PredicatableDsl;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.objects.dsl.ObjectBindingDsl;
import com.niton.reactj.objects.dsl.ObjectConvertingConsumerDsl;

import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectConvertingConsumerDslImpl<F, O> implements ObjectConvertingConsumerDsl<F, O> {
    private final ConvertingConsumerDsl<F> impl;
    private final EventEmitter<O>          objectChangeEvent;
    private final Supplier<O>              objectSupplier;

    public ObjectConvertingConsumerDslImpl(
            ConvertingConsumerDsl<F> impl,
            EventEmitter<O> objectChangeEvent,
            Supplier<O> objectSupplier
    ) {
        this.impl              = impl;
        this.objectChangeEvent = objectChangeEvent;
        this.objectSupplier    = objectSupplier;
    }

    @Override
    public PredicatableDsl<F> from(EventEmitter<? extends F> event) {
        return impl.from(event);
    }

    @Override
    public PredicatableDsl<O> onModelChange(Function<O, F> getter) {
        return from(getter).from(objectChangeEvent);
    }

    @Override
    public ObjectBindingDsl<F> from(Supplier<? extends F> source) {
        return new ObjectBindingDslImpl<>(impl.from(source), objectChangeEvent);
    }

    @Override
    public <S> ObjectConvertingConsumerDsl<S, O> from(Function<S, ? extends F> converter) {
        return new ObjectConvertingConsumerDslImpl<>(
                impl.from(converter),
                objectChangeEvent,
                objectSupplier
        );
    }

    public ObjectBindingDsl<O> fromModel(Function<O, F> converter) {
        return from(converter).from(objectSupplier);
    }
}
