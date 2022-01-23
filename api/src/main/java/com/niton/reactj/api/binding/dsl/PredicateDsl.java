package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

/**
 * A DSL node or extension that allows to further extend a condition with logical AND/OR operations.
 *
 * @param <T> the type the predicate is working on
 */
public interface PredicateDsl<T> extends ConditionDsl {
    @Override
    default PredicateDsl<T> or(Condition value) {
        return or(value.toPredicate());
    }

    /**
     * Combines the previous condition with the given condition using the logical OR operator.
     *
     * @param value The condition to "or" with the previous condition.
     */
    PredicateDsl<T> or(Predicate<? super T> value);

    @Override
    default PredicateDsl<T> and(Condition value) {
        return and(value.toPredicate());
    }

    /**
     * Combines the previous condition with the given condition using the logical AND operator.
     *
     * @param value The condition to "and" with the previous condition.
     */
    PredicateDsl<T> and(Predicate<? super T> value);
}
