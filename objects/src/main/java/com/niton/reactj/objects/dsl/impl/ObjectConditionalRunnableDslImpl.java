package com.niton.reactj.objects.dsl.impl;

import com.niton.reactj.api.event.Listenable;
import com.niton.reactj.api.binding.dsl.ConditionalRunnableDsl;
import com.niton.reactj.api.binding.dsl.MultiListenerDsl;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.objects.dsl.ObjectConditionalRunnableDsl;

public class ObjectConditionalRunnableDslImpl implements ObjectConditionalRunnableDsl {
    private final ConditionalRunnableDsl impl;
    private final EventEmitter<?>        modelChange;

    public ObjectConditionalRunnableDslImpl(
            ConditionalRunnableDsl impl,
            EventEmitter<?> modelChange
    ) {
        this.impl        = impl;
        this.modelChange = modelChange;
    }

    @Override
    public ConditionalRunnableDsl or(Condition condition) {
        return new ObjectConditionalRunnableDslImpl(impl.or(condition), modelChange);
    }

    @Override
    public ConditionalRunnableDsl and(Condition condition) {
        return new ObjectConditionalRunnableDslImpl(impl.and(condition), modelChange);
    }

    @Override
    public Runnable build() {
        return impl.build();
    }

    @Override
    public MultiListenerDsl on(Listenable event) {
        return impl.on(event);
    }

    @Override
    public MultiListenerDsl onModelChange() {
        return impl.on(modelChange);
    }
}
