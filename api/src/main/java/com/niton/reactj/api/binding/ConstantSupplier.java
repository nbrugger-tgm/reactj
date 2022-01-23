package com.niton.reactj.api.binding;

import java.util.function.Supplier;

public class ConstantSupplier<T> implements Supplier<T> {
    private final T value;

    public ConstantSupplier(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
