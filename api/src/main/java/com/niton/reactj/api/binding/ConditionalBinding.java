package com.niton.reactj.api.binding;

import com.niton.reactj.api.binding.runnable.PredicatedRunnable;

import java.util.function.Predicate;

/**
 * A {@link Binding} that is only executed if a given {@link Predicate} is true.
 *
 * @param <T> The type of binding and predicate
 */
public class ConditionalBinding<T> extends Binding<T> implements PredicatedRunnable<T> {
    /**
     * The predicate to check if the binding should be executed
     */
    private Predicate<T> predicate;

    public ConditionalBinding(Binding<T> binding, Predicate<? super T> predicate) {
        super(binding.consumer, binding.source);
        this.predicate = predicate::test;
    }

    @Override
    public void run() {
        T val = source.get();
        if (predicate.test(val))
            consumer.accept(val);
    }

    @Override
    public Predicate<T> getPredicate() {
        return predicate;
    }

    @Override
    public void setPredicate(Predicate<? super T> predicate) {
        this.predicate = predicate::test;
    }
}
