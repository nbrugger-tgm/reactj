package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.runnable.PredicatedRunnable;

import java.util.function.Predicate;

/**
 * Enables you to further configure a conditional binding with logical operators.
 *
 * @param <T> the type of the binding
 */
public interface ConditionalBindingDsl<T>
        extends PredicateDsl<T>, ConditionalRunnableDsl {

    /**
     * Returns the {@link PredicatedRunnable}, that the dsl crafted so far.
     */
    @Override
    PredicatedRunnable<T> build();

    @Override
    default ConditionalBindingDsl<T> or(Condition value) {
        return or(e -> value.check());
    }

    @Override
    ConditionalBindingDsl<T> or(Predicate<? super T> value);

    @Override
    default ConditionalBindingDsl<T> and(Condition value) {
        return and(e -> value.check());
    }

    @Override
    ConditionalBindingDsl<T> and(Predicate<? super T> value);


}
