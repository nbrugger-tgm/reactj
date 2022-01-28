package com.niton.reactj.objects.dsl.impl;

import com.niton.reactj.api.event.Listenable;
import com.niton.reactj.api.binding.dsl.BindingDsl;
import com.niton.reactj.api.binding.dsl.MultiListenerDsl;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.objects.dsl.ObjectBindingDsl;
import com.niton.reactj.objects.dsl.ObjectConditionalBindingDsl;
import com.niton.reactj.objects.dsl.ObjectRunnableDsl;

import java.util.function.Predicate;

public class ObjectBindingDslImpl<N, O> implements ObjectBindingDsl<N> {
    private final BindingDsl<N>   impl;
    private final EventEmitter<O> objectChange;

    public ObjectBindingDslImpl(
            BindingDsl<N> impl,
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
    public Runnable build() {
        return impl.build();
    }

    @Override
    public ObjectConditionalBindingDsl<N> when(Predicate<? super N> predicate) {
        return new ObjectConditionalBindingDslImpl<>(impl.when(predicate), objectChange);
    }

    @Override
    public ObjectConditionalBindingDsl<N> when(Condition condition) {
        return new ObjectConditionalBindingDslImpl<>(impl.when(condition), objectChange);
    }

    @Override
    public ObjectRunnableDsl and(Runnable runnable) {
        return new ObjectRunnableDslImpl(impl.and(runnable), objectChange);
    }

    @Override
    public MultiListenerDsl onModelChange() {
        return impl.on(objectChange);
    }
}
