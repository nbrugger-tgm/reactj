package com.niton.reactj.api.binding.runnable;

import com.niton.reactj.api.binding.predicates.HasPredicate;

/**
 * A Runnable that can be executed if a predicate is true
 *
 * @param <T> the type for the predicate
 */
public interface PredicatedRunnable<T> extends Runnable, HasPredicate<T> {
}
