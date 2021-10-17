package com.niton.reactj.api.binding;

import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.predicates.HasCondition;
import com.niton.reactj.api.binding.predicates.HasPredicate;

import java.util.function.Predicate;

public class ConditionalReactiveBinding<T> implements Runnable, HasCondition, HasPredicate<T> {

	private final ReactiveBinding<T> binding;
	private       Predicate<T>       predicate = i -> true;
	private       Condition          condition = Condition.YES;

	public ConditionalReactiveBinding(ReactiveBinding<T> binding) {
		this.binding = binding;
	}

	@Override
	public void run() {
		T val = binding.source.get();
		if (condition.check() && predicate.test(val))
			binding.sink.accept(val);
	}

	@Override
	public Condition getCondition() {
		return condition;
	}

	@Override
	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	@Override
	public Predicate<T> getPredicate() {
		return predicate;
	}

	@Override
	public void setPredicate(Predicate<T> predicate) {
		this.predicate = predicate;
	}
}
