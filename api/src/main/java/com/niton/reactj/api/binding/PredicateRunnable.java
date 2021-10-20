package com.niton.reactj.api.binding;

import com.niton.reactj.api.binding.predicates.HasPredicate;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class PredicateRunnable<T> implements HasPredicate<T>, Runnable {
	private final Supplier<T>  source;
	private final Runnable     runnable;
	private       Predicate<T> predicate = i -> true;

	public PredicateRunnable(
			ReactiveBinding<T> binding,
			RunnableGroup runnables
	) {
		this.runnable = runnables;
		this.source   = binding.source;
	}

	@Override
	public void run() {
		if (getPredicate().test(source.get())) {
			runnable.run();
		}
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
