package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.BaseBinding;
import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

/**
 * Used to modify a {@link BaseBinding} and build something different or
 * more complex.
 *
 * @param <T>
 */
public interface BindingDsl<T> extends RunnableDsl, ConditionalDsl, PredicatableDsl<T> {
    @Override
    default ConditionalBindingDsl<T> when(Condition condition) {
        return when(o -> condition.check());
    }

    @Override
    ConditionalBindingDsl<T> when(Predicate<? super T> predicate);

    /**
     * After calling this method you should not further use this DSL node since this could cause
     * side effects and modify the returned value! <i>The {@link BinderDsl} on the other hand
     * can still be used to start a new DSL chain call.</i>
     *
     * @return the current product of the DSL chain as {@link Runnable}
     */
    @Override
    Runnable build();
}
