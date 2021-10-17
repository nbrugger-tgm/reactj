package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.ConditionalReactiveBinding;
import com.niton.reactj.api.binding.ReactiveBinding;
import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

public class BindingCallBuilder<T> extends RunnableCallBuilder {
	public BindingCallBuilder(ReactiveBinding<T> r, BindingBuilder rootBuilder) {
		super(r, rootBuilder);
	}

	@Override
	public ConditionBindingBuilder<T> when(Condition condition) {
		//suppress because the constructor ensures the type
		@SuppressWarnings("unchecked")
		ConditionalReactiveBinding<T> binding = new ConditionalReactiveBinding<>((ReactiveBinding<T>) runnable);

		binding.setCondition(condition);

		return new ConditionBindingBuilder<>(binding, rootBuilder);
	}

	public ConditionBindingBuilder<T> when(Predicate<T> condition) {
		//suppress because the constructor ensures the type
		@SuppressWarnings("unchecked")
		ConditionalReactiveBinding<T> binding = new ConditionalReactiveBinding<>((ReactiveBinding<T>) runnable);

		binding.setPredicate(condition);

		return new ConditionBindingBuilder<>(binding, rootBuilder);
	}
}
