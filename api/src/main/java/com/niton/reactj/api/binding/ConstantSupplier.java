package com.niton.reactj.api.binding;

import java.util.function.Supplier;

/**
 * A supplier that returns the same value every time.
 *
 * @param <T> the type to supply
 */
public class ConstantSupplier<T> implements Supplier<T> {
    /**
     * The value to return each time the supplier is called.
     */
    private final T value;

    public ConstantSupplier(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
