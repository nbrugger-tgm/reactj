package com.niton.reactj.api.binding.predicates;

import java.util.function.Predicate;

public interface HasPredicate<T> {
	Predicate<T> getPredicate();

	void setPredicate(Predicate<T> predicate);
}
