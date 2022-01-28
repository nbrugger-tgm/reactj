package com.niton.reactj.api.binding;

import com.niton.reactj.api.binding.consumer.ConditionalConsumer;
import com.niton.reactj.api.binding.runnable.PredicatedRunnable;

import java.util.function.Predicate;

/**
 * A {@link BaseBinding} that is only executed if a given {@link Predicate} is true.
 *
 * @param <T> The type of binding and predicate
 */
public class ConditionalBinding<T> extends ConditionalConsumer<T>
        implements Binding<T>, PredicatedRunnable<T> {
    /**
     * The binding to execute if the predicate is true
     */
    private final Binding<T>           binding;
    /**
     * The predicate to check if the binding should be executed
     */
    private       Predicate<? super T> predicate;

    public ConditionalBinding(Binding<T> binding, Predicate<? super T> predicate) {
        super(binding, predicate);
        this.predicate = predicate;
        this.binding   = binding;
    }

    @Override
    public Predicate<T> getPredicate() {
        return predicate::test;
    }

    @Override
    public void setPredicate(Predicate<? super T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public T get() {
        return binding.get();
    }
}
