package com.niton.reactj.objects.dsl.impl;

import com.niton.reactj.api.binding.Listenable;
import com.niton.reactj.api.binding.dsl.MultiListenerDsl;
import com.niton.reactj.api.binding.dsl.RunnableDsl;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.objects.dsl.ObjectConditionalRunnableDsl;
import com.niton.reactj.objects.dsl.ObjectRunnableDsl;

public class ObjectRunnableDslImpl implements ObjectRunnableDsl {
    private final RunnableDsl     impl;
    private final EventEmitter<?> modelChange;

    public ObjectRunnableDslImpl(RunnableDsl impl, EventEmitter<?> modelChange) {
        this.impl        = impl;
        this.modelChange = modelChange;
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
    public MultiListenerDsl onModelChange() {
        return impl.on(modelChange);
    }

    @Override
    public ObjectRunnableDsl and(Runnable runnable) {
        return new ObjectRunnableDslImpl(impl.and(runnable), modelChange);
    }

    @Override
    public ObjectConditionalRunnableDsl when(Condition condition) {
        return new ObjectConditionalRunnableDslImpl(impl.when(condition), modelChange);
    }
}
