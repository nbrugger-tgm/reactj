package com.niton.reactj.api.binding.dsl;

import java.util.function.Predicate;

public interface PredicatableDsl<T> {
    PredicateDsl<T> when(Predicate<? super T> predicate);
}
