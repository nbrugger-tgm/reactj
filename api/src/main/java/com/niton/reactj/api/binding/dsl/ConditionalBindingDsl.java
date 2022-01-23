package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.runnable.PredicatedRunnable;

import java.util.function.Predicate;

public interface ConditionalBindingDsl<T>
        extends PredicateDsl<T>, ConditionalRunnableDsl {

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
