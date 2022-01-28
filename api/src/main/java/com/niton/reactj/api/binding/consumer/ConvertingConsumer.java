package com.niton.reactj.api.binding.consumer;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A consumer that converts the value it receives.
 *
 * @param <F> FROM, the type it converts from
 * @param <T> TO, the type it converts to
 */
public class ConvertingConsumer<F, T> implements Consumer<F> {
    /**
     * This consumer will be called with the converted value.
     */
    private final Consumer<T>    target;
    /**
     * This function acts as a converter between the types.
     */
    private final Function<F, T> converter;

    /**
     * @param target    {@link #target}
     * @param converter {@link #converter}
     */
    public ConvertingConsumer(Consumer<T> target, Function<F, T> converter) {
        this.target = target;
        if (converter == null)
            throw new IllegalArgumentException("converter cannot be null");
        this.converter = converter;
    }

    @Override
    public void accept(F value) {
        target.accept(converter.apply(value));
    }
}
