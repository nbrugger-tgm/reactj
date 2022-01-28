package com.niton.reactj.core.impl.dsl;

import com.niton.reactj.api.binding.dsl.ConditionalBindingDsl;
import com.niton.reactj.api.binding.dsl.MultiListenerDsl;
import com.niton.reactj.api.binding.runnable.PredicatedRunnable;
import com.niton.reactj.api.event.Listenable;

import java.util.function.Predicate;

public class CoreConditionalBindingDsl<T> implements ConditionalBindingDsl<T> {
    private final PredicatedRunnable<T> binding;

    public CoreConditionalBindingDsl(PredicatedRunnable<T> binding) {
        this.binding = binding;
    }

    @Override
    public PredicatedRunnable<T> build() {
        return binding;
    }

    @Override
    public ConditionalBindingDsl<T> or(Predicate<? super T> value) {
        binding.setPredicate(binding.getPredicate().or(value));
        return this;
    }

    @Override
    public ConditionalBindingDsl<T> and(Predicate<? super T> value) {
        binding.setPredicate(binding.getPredicate().and(value));
        return this;
    }

    @Override
    public MultiListenerDsl on(Listenable event) {
        event.listen(binding);
        return this::on;
    }
}
