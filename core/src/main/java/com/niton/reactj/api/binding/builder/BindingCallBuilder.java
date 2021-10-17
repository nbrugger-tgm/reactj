package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.ConditionalBindingWithRunnables;
import com.niton.reactj.api.binding.ReactiveBinding;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

import java.util.function.Predicate;

public class BindingCallBuilder<T> extends BaseRunnableBuilder<RunnableGroup> {
	private final ReactiveBinding<T> binding;

	public BindingCallBuilder(ReactiveBinding<T> binding, BindingBuilder rootBuilder) {
		super(new RunnableGroup(binding), rootBuilder);
		this.binding = binding;
	}

	/**
	 * same as {@link RunnableCallBuilder#when(Condition)}
	 */
	public ConditionBindingBuilder<T> when(Condition condition) {
		ConditionalBindingWithRunnables<T> conditional = new ConditionalBindingWithRunnables<>(binding, runnable);
		conditional.setPredicate(i -> condition.check());
		return new ConditionBindingBuilder<>(conditional, rootBuilder);
	}


	/**
	 * Same as {@link RunnableCallBuilder#when(Condition)}, but using a {@link Predicate} as condition
	 */
	public ConditionBindingBuilder<T> when(Predicate<T> condition) {
		ConditionalBindingWithRunnables<T> conditional = new ConditionalBindingWithRunnables<>(binding, runnable);
		conditional.setPredicate(condition);
		return new ConditionBindingBuilder<>(conditional, rootBuilder);
	}

	/**
	 * Adds a runnable to this group to be executed too
	 */
	public BindingCallBuilder<T> and(Runnable runnable) {
		this.runnable.add(runnable);
		return this;
	}
}
