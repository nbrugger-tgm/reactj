package com.niton.reactj.objects.dsl;

import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.objects.dsl.impl.ObjectDslImpl;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A DSL flavor of {@link BinderDsl} that is tailored to operate on a specific object.
 *
 * @param <T> The type of the object that is the target of some bindings.
 */
public interface ObjectDsl<T> extends BinderDsl {

    static <T> ObjectDsl<T> create(Supplier<T> object, EventEmitter<T> emitter) {
        return new ObjectDslImpl<>(object, emitter);
    }

    @Override
    ObjectRunnableDsl call(Runnable runnable);

    @Override
    default <C> ObjectBindingDsl<C> bind(Consumer<C> setter, Supplier<C> getter) {
        return call(setter).with(getter);
    }

    @Override
    <N> ObjectConsumerDsl<N, T> call(Consumer<N> runnable);

    @Override
    default <C, S> ObjectBindingDsl<S> bind(
            Consumer<C> setter, Supplier<S> getter, Function<S, C> converter
    ) {
        return call(setter).with(converter).from(getter);
    }

    default <P> ObjectBindingDsl<P> bind(BiConsumer<T, P> setter, Supplier<P> getter) {
        return call(setter).with(getter);
    }

    <P> ObjectConsumerDsl<P, T> call(BiConsumer<T, P> setter);

    default <P, S> ObjectBindingDsl<S> bind(
            BiConsumer<T, P> setter, Supplier<S> getter, Function<S, P> converter
    ) {
        return call(setter).with(converter).from(getter);
    }

    default <C> ObjectBindingDsl<T> bind(Consumer<C> setter, Function<T, C> getter) {
        return call(setter).withModel(getter);
    }

}
