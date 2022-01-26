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

    /**
     * Creates a new {@link ObjectDsl} instance that is bound to the a model object.
     *
     * @param object  the object to bind the DSL to (used in functions like {@link
     *                #call(BiConsumer)})
     * @param emitter an {@link EventEmitter} that is already attached to the model object and
     *                will fire events when the model object changes
     */
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


    /**
     * Creates a binding that will call setter with the converted result of the getter begin
     * called upon the model object.
     *
     * @param setter    the setter to call
     * @param getter    the getter to get a value from the model object
     * @param converter converts the value of the getter to the setter
     * @param <P>       the "real" type of the model parameter
     * @param <F>       the converted type of the model parameter
     */
    default <P, F> ObjectBindingDsl<T> bind(
            Consumer<F> setter, Function<T, P> getter, Function<P, F> converter
    ) {
        return call(setter).with(converter).fromObject(getter);
    }

    /**
     * Same as {@link #call(BiConsumer)} but directly attaching a getter
     *
     * @param getter the getter to get the value for the setter from
     */
    default <P> ObjectBindingDsl<P> bind(BiConsumer<T, P> setter, Supplier<P> getter) {
        return call(setter).with(getter);
    }

    /**
     * Calls the setter of the model object with the value of the getter under conditions defined
     * later in the DSL.
     *
     * @param setter the setter of the model object to call (e.g. {@code MyModel::setSomething})
     * @param <P>    the type of the setter parameter
     */
    <P> ObjectConsumerDsl<P, T> call(BiConsumer<T, P> setter);


    /**
     * Same as {@link #bind(BiConsumer, Supplier)} but directly attaching a converter
     *
     * @param converter the converter to convert the value of the getter to the setter
     */
    default <P, S> ObjectBindingDsl<S> bind(
            BiConsumer<T, P> setter, Supplier<S> getter, Function<S, P> converter
    ) {
        return call(setter).with(converter).from(getter);
    }

    /**
     * Calls the setter with the value of the getter under conditions defined later in the DSL.
     * What sets this method apart from {@link #bind(Consumer, Supplier)} is that the getter
     * directly refers to the model object. So use {@code MyModel::getSomething} as the getter
     * without specifying the model object itself.
     *
     * @param setter the setter to call
     * @param getter the getter of the model object to call
     * @param <C>    the type of the value to set
     */
    default <C> ObjectBindingDsl<T> bind(Consumer<C> setter, Function<T, C> getter) {
        return call(setter).withModel(getter);
    }

}
