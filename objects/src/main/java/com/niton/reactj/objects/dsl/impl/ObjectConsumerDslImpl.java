package com.niton.reactj.objects.dsl.impl;

import com.niton.reactj.api.binding.dsl.ConsumerDsl;
import com.niton.reactj.api.binding.dsl.PredicatableDsl;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.objects.dsl.ObjectBindingDsl;
import com.niton.reactj.objects.dsl.ObjectConsumerDsl;
import com.niton.reactj.objects.dsl.ObjectConvertingConsumerDsl;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectConsumerDslImpl<N, O> implements ObjectConsumerDsl<N, O> {
    private final ConsumerDsl<N>  impl;
    private final EventEmitter<O> objectChangeEvent;
    private final Supplier<O>     objectSupplier;

    public ObjectConsumerDslImpl(
            ConsumerDsl<N> impl,
            EventEmitter<O> objectChangeEvent,
            Supplier<O> objectSupplier
    ) {
        this.impl              = impl;
        this.objectChangeEvent = objectChangeEvent;
        this.objectSupplier    = objectSupplier;
    }

    @Override
    public PredicatableDsl<N> on(EventEmitter<? extends N> event) {
        return impl.on(event);
    }

    @Override
    public ObjectConsumerDsl<N, O> and(Consumer<? super N> consumer) {
        return new ObjectConsumerDslImpl<>(impl.and(consumer), objectChangeEvent, objectSupplier);
    }

    @Override
    public <N1 extends N> ObjectConsumerDsl<N1, O> andCall(Consumer<N1> consumer) {
        return new ObjectConsumerDslImpl<>(
                impl.andCall(consumer),
                objectChangeEvent,
                objectSupplier
        );
    }

    @Override
    public <F> ObjectConvertingConsumerDsl<F, O> with(Function<F, N> converter) {
        return new ObjectConvertingConsumerDslImpl<>(
                impl.with(converter),
                objectChangeEvent,
                objectSupplier
        );
    }

    @Override
    public ObjectBindingDsl<N> withValue(N constant) {
        return new ObjectBindingDslImpl<>(impl.withValue(constant), objectChangeEvent);
    }

    @Override
    public ObjectBindingDsl<N> with(Supplier<? extends N> source) {
        return new ObjectBindingDslImpl<>(impl.with(source), objectChangeEvent);
    }

    @Override
    public ObjectBindingDsl<O> withModel(Function<O, N> converter) {
        return new ObjectBindingDslImpl<>(
                impl.with(converter).from(objectSupplier),
                objectChangeEvent
        );
    }

    @Override
    public PredicatableDsl<O> onObjectChange(Function<O, N> getter) {
        return with(getter).from(objectChangeEvent);
    }
}
