package com.niton.reactj.api.binding.predicates;

import java.util.function.Predicate;

/**
 * Implement to indicate that the object is only executable in the presence of a passing predicate
 *
 * @param <T> the type of the {@link Predicate}
 */
public interface HasPredicate<T> {
    /**
     * @return the predicate that controls the execution of this object
     */
    Predicate<T> getPredicate();

    /**
     * @param predicate a predicate that determines if the object is allowed to execute
     */
    void setPredicate(Predicate<? super T> predicate);
}
