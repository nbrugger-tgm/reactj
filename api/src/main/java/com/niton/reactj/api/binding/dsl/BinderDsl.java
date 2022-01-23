package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.exceptions.Exceptions;

import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface BinderDsl {
    static BinderDsl create() {
        return ServiceLoader.load(BinderDsl.class)
                            .findFirst()
                            .orElseThrow(Exceptions.noImplementation(BinderDsl.class));
    }

    RunnableDsl call(Runnable runnable);

    default <T> BindingDsl<T> bind(Consumer<T> setter, Supplier<T> getter) {
        return call(setter).with(getter);
    }

    <T> ConsumerDsl<T> call(Consumer<T> runnable);

    default <C, S> BindingDsl<S> bind(
            Consumer<C> setter,
            Supplier<S> getter,
            Function<S, C> converter
    ) {
        return call(setter).with(converter).from(getter);
    }

}
