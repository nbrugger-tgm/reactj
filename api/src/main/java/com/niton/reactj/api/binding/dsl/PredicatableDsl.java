package com.niton.reactj.api.binding.dsl;

import java.util.function.Predicate;

/**
 * A exetension for DSL nodes that want the possibility to make their runnable/consumer/binding
 * conditionally
 *
 * @param <T> the type to use for the predicates
 */
public interface PredicatableDsl<T> {
    /**
     * Adds a predicate to the current node, therefore the node will only run if the predicate is
     * true at the time of execution. The value the predicate is evaluated against is the value
     * that will be used for the previously defined consumer/binding.
     *
     * @param predicate the predicate oppose to the value
     */
    PredicateDsl<T> when(Predicate<? super T> predicate);
}
