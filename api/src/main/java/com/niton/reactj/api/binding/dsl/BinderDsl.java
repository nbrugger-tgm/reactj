package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.event.Listenable;
import com.niton.reactj.api.exceptions.Exceptions;

import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A DSL that can be used to craft bindings between functions.
 * <p>
 * All functions provided by the DSL return the next part of the call chain. To make use of the
 * functions, you have to complete the chain with one of the following statements:
 * <ul>
 *     <li>{@link ListenerDsl#on(Listenable)} </li>
 *     <li>{@link MultiListenerDsl#andOn(Listenable)} (Listenable)} </li>
 *     <li>any {@code build()} method</li>
 *     <li>{@link ConsumerDsl#on(EventEmitter)}</li>
 *     <li>{@link ConvertingConsumerDsl#from(EventEmitter)}</li>
 * </ul>
 * <p>
 *     All instance-functions return the next part of the call chain. To make use of the functions.
 *     therefore {@code @return the chain to continue with} is omitted from the javaDoc.
 * </p>
 *
 * @see Consumer
 * @see Function
 * @see Supplier
 * @see <a href="https://de.wikipedia.org/wiki/Domänenspezifische_Sprache">DSL - Wikipedia</a>
 */
public interface BinderDsl {
    /**
     * Creates a <i>reusable</i> instance of an BinderDsl. <br>
     * In oder for this to work, the {@link BinderDsl} must be provided in the {@link
     * ServiceLoader}. More information on SPI with java 9+ and JPMS :
     * <a href="https://www.baeldung.com/java-9-modularity#7-provides--with">Baleung - Java 9
     * Modules</a>
     * <p><i>We recommend not to provide an implementation yourself but use the core:impl
     * module (shipped with the core module) since our internal libraries are tested against this
     * implementation. Also it is easier since you don't need to implement the interface yourself
     * </i></p>
     *
     * @return an instance of a reusable {@link BinderDsl} with unknown implementation
     */
    static BinderDsl create() {
        return ServiceLoader.load(BinderDsl.class)
                            .findFirst()
                            .orElseThrow(Exceptions.noImplementation(BinderDsl.class));
    }

    /**
     * Call the runnable, under conditions defined later in the chain
     */
    RunnableDsl call(Runnable runnable);

    /**
     * This is a shorthand that binds the getter to the setter, meaning the setter will be called
     * with the value the setter provides.
     *
     * @param setter the setter to call with the value of the getter
     * @param getter the getter to get the value from
     * @param <T>    the type of getter+setter
     */
    default <T> BindingDsl<T> bind(Consumer<T> setter, Supplier<T> getter) {
        return call(setter).with(getter);
    }

    /**
     * Call the consumer, under conditions defined later in the chain.
     * <p>
     * The value to call the consumer with is defined later in the chain as well.
     * </p>
     *
     * @param consumer the consumer to call
     * @param <T>      the type  the consumer can consume
     */
    <T> ConsumerDsl<T> call(Consumer<T> consumer);

    /**
     * Call the consumer with the converted value from the getter, under conditions defined later
     * in the chain.
     *
     * @param setter    the setter to get the value from
     * @param getter    the getter to feed the value into
     * @param converter the converter to convert the value from the getter so that it matches the
     *                  type the consumer requires
     * @param <C>       the accepted by the consumer
     * @param <S>       the type provided by the getter
     */
    default <C, S> BindingDsl<S> bind(
            Consumer<C> setter,
            Supplier<S> getter,
            Function<S, C> converter
    ) {
        return call(setter).with(converter).from(getter);
    }


    /**
     * If recursion prevention is enabled (default), this method will prevent a binding to trigger
     * calling itself.<br>
     * This is usefull in a scenario where you have a UI binding that changes the object and vice
     * versa. This can lead to a stack overflow
     * {@code UI event -> update model -> Observer calls binding -> binding updates UI -> Ui fires
     * event -> updates the model ...}
     *
     * @param preventRecursion if true, recursion prevention is enabled
     */
    void setRecursionPrevention(boolean preventRecursion);

}
