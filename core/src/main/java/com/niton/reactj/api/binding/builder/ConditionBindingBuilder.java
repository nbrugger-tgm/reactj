package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.ConditionalBindingWithRunnables;
import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

public class ConditionBindingBuilder<T> extends BaseRunnableBuilder<ConditionalBindingWithRunnables<T>> {

	public ConditionBindingBuilder(
			ConditionalBindingWithRunnables<T> binding,
			BindingBuilder rootBuilder
	) {
		super(binding, rootBuilder);
	}

	/**
	 * {@link ConditionRunnableBuilder#or(Condition)}
	 */
	public ConditionBindingBuilder<T> or(Condition condition) {
		runnable.setPredicate(runnable.getPredicate().or(i -> condition.check()));
		return this;
	}

	/**
	 * {@link ConditionRunnableBuilder#and(Condition)}
	 */
	public ConditionBindingBuilder<T> and(Condition condition) {
		runnable.setPredicate(runnable.getPredicate().and(i -> condition.check()));
		return this;
	}

	/**
	 * execute if the previous condition or this predicate applies
	 */
	public ConditionBindingBuilder<T> or(Predicate<T> condition) {
		runnable.setPredicate(runnable.getPredicate().or(condition));
		return this;
	}

	/**
	 * execute if the previous condition and this predicate applies
	 */
	public ConditionBindingBuilder<T> and(Predicate<T> condition) {
		runnable.setPredicate(runnable.getPredicate().and(condition));
		return this;
	}
}
