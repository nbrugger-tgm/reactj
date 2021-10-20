package com.niton.reactj.api.binding.builder.exposed;

import com.niton.reactj.api.binding.builder.conditional.ConditionalRunnableBuilder;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.predicates.HasPredicate;

import java.util.function.Predicate;

/**
 * Used to add the "and" and "or" methods to a builder
 *
 * @param <T> the type of the reactive binding
 * @param <R> the builder to be returned
 */
public interface ConditionalBindingBuilder<T, R> {
	/**
	 * {@link ConditionalRunnableBuilder#or(Condition)}
	 */
	default R or(Condition condition) {
		getBinding().setPredicate(getBinding().getPredicate().or(i -> condition.check()));
		return getThis();
	}

	HasPredicate<T> getBinding();

	R getThis();

	/**
	 * {@link ConditionalRunnableBuilder#and(Condition)}
	 */
	default R and(Condition condition) {
		getBinding().setPredicate(getBinding().getPredicate().and(i -> condition.check()));
		return getThis();
	}

	/**
	 * execute if the previous condition or this predicate applies
	 */
	default R or(Predicate<T> condition) {
		getBinding().setPredicate(getBinding().getPredicate().or(condition));
		return getThis();
	}

	/**
	 * execute if the previous condition and this predicate applies
	 */
	default R and(Predicate<T> condition) {
		getBinding().setPredicate(getBinding().getPredicate().and(condition));
		return getThis();
	}
}
