package com.niton.reactj.api.binding.runnable;

import com.niton.reactj.api.binding.predicates.HasPredicate;

public interface PredicatedRunnable<T> extends Runnable, HasPredicate<T> {
}
