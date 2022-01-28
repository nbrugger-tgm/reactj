package com.niton.reactj.api.binding;

import com.niton.reactj.api.binding.runnable.NonCyclicRunnable;

/**
 * The binding variant of {@link com.niton.reactj.api.binding.runnable.NonCyclicRunnable}
 *
 * @param <T> the type of the binding
 */
public class NonCyclicBinding<T> extends NonCyclicRunnable implements Binding<T> {

    private final Binding<T> binding;

    /**
     * @param binding the underlying binding to forward to
     */
    public NonCyclicBinding(Binding<T> binding) {
        super(binding);
        this.binding = binding;
    }

    @Override
    public void accept(T t) {
        binding.accept(t);
    }

    @Override
    public T get() {
        return binding.get();
    }
}
