package com.niton.reactj.api.binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A Binding is a pipe from one function to another, a consumer to a supplier.
 *
 * @param <T> the type of the piped values
 */
public interface Binding<T> extends Runnable, Consumer<T>, Supplier<T> {
    /**
     * calls {@link #accept(Object)} with the value of {@link #get()}
     */
    @Override
    default void run() {
        accept(get());
    }
}
