package com.niton.reactj.objects.dsl.impl;

import com.niton.reactj.api.binding.Listenable;
import com.niton.reactj.api.binding.dsl.ConditionalBindingDsl;
import com.niton.reactj.api.binding.dsl.MultiListenerDsl;
import com.niton.reactj.api.binding.runnable.PredicatedRunnable;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.objects.dsl.ObjectConditionalBindingDsl;

import java.util.function.Predicate;

public class ObjectConditionalBindingDslImpl<N, O> implements ObjectConditionalBindingDsl<N> {
    private final ConditionalBindingDsl<N> impl;
    private final EventEmitter<O>          objectChange;

    public ObjectConditionalBindingDslImpl(
            ConditionalBindingDsl<N> impl,
            EventEmitter<O> objectChange
    ) {
        this.impl         = impl;
        this.objectChange = objectChange;
    }

    @Override
    public MultiListenerDsl on(Listenable event) {
        return impl.on(event);
    }

    @Override
    public PredicatedRunnable<N> build() {
        return impl.build();
    }

    @Override
    public ConditionalBindingDsl<N> or(Predicate<? super N> value) {
        return new ObjectConditionalBindingDslImpl<>(impl.or(value), objectChange);
    }

    @Override
    public ConditionalBindingDsl<N> and(Predicate<? super N> value) {
        return new ObjectConditionalBindingDslImpl<>(impl.and(value), objectChange);
    }

    @Override
    public MultiListenerDsl onModelChange() {
        return impl.on(objectChange);
    }
}
