package com.niton.reactj.api.binding;

import com.niton.reactj.api.binding.predicates.HasPredicate;

import java.util.function.Predicate;

public class ConditionalBinding<T> implements Runnable, HasPredicate<T> {

	protected final ReactiveBinding<T> binding;
	private         Predicate<T>       predicate = i -> true;

	public ConditionalBinding(ReactiveBinding<T> binding) {
		this.binding = binding;
	}

	@Override
	public void run() {
		T val = binding.source.get();
		if (predicate.test(val))
			binding.consumer.accept(val);
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
