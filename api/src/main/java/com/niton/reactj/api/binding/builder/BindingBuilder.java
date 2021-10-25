package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.PredicateRunnable;
import com.niton.reactj.api.binding.builder.exposed.ExposedBindingBuilder;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.implementation.binding.RunnableBuilder;

import java.util.function.Predicate;

public interface BindingBuilder<T, R> extends ExposedBindingBuilder<T, R> {
	/**
	 * same as {@link RunnableBuilder#when(Condition)}
	 */
	default R when(Condition condition) {
		var conditional = createConditionalBinding();
		conditional.setPredicate(i -> condition.check());
		return getConditionalReturn(conditional);
	}

	PredicateRunnable<T> createConditionalBinding();

	R getConditionalReturn(PredicateRunnable<T> conditional);

	/**
	 * Same as {@link RunnableBuilder#when(Condition)}, but using a {@link Predicate} as
	 * condition
	 */
	default R when(Predicate<T> condition) {
		var conditional = createConditionalBinding();
		conditional.setPredicate(condition);
		return getConditionalReturn(conditional);
	}
}
