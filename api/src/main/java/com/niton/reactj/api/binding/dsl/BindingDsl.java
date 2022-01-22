package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

public interface BindingDsl<T> extends RunnableDsl, ConditionalDsl, PredicatableDsl<T> {
	@Override
	default ConditionalBindingDsl<T> when(Condition condition) {
		return when(o -> condition.check());
	}

	@Override
	ConditionalBindingDsl<T> when(Predicate<? super T> predicate);

	Runnable build();
}
