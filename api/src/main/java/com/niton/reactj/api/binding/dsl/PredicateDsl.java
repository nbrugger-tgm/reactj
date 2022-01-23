package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

public interface PredicateDsl<T> extends ConditionDsl {
	default PredicateDsl<T> or(Condition value) {
		return or(value.toPredicate());
	}

	PredicateDsl<T> or(Predicate<? super T> value);

	default PredicateDsl<T> and(Condition value) {
		return and(value.toPredicate());
	}

	PredicateDsl<T> and(Predicate<? super T> value);
}
