package com.niton.reactj.objects.dsl.impl;

import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.objects.dsl.ObjectConsumerDsl;
import com.niton.reactj.objects.dsl.ObjectDsl;
import com.niton.reactj.objects.dsl.ObjectRunnableDsl;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObjectDslImpl<T> implements ObjectDsl<T> {
    private final Supplier<T>     model;
    private final EventEmitter<T> objectChangeEvent;
    private final BinderDsl       impl = BinderDsl.create();

    public ObjectDslImpl(Supplier<T> model, EventEmitter<T> objectChangeEvent) {
        this.model             = model;
        this.objectChangeEvent = objectChangeEvent;
    }

    @Override
    public ObjectRunnableDsl call(Runnable runnable) {
        return new ObjectRunnableDslImpl(impl.call(runnable), objectChangeEvent);
    }

    @Override
    public <N> ObjectConsumerDsl<N, T> call(Consumer<N> runnable) {
        return new ObjectConsumerDslImpl<>(impl.call(runnable), objectChangeEvent, model);
    }

    @Override
    public <P> ObjectConsumerDsl<P, T> call(BiConsumer<T, P> setter) {
        return call(v -> setter.accept(model.get(), v));
    }
}
