package com.niton.reactj.api.binding.runnable;

import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.predicates.HasCondition;

public class ConditionalRunnable implements Runnable, HasCondition {
	private final Runnable  target;
	private       Condition condition;

	public ConditionalRunnable(Condition condition, Runnable target) {
		this.condition = condition;
		this.target    = target;
	}

	public Condition getCondition() {
		return condition;
	}

	@Override
	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	@Override
	public void run() {
		if (condition.check())
			target.run();
	}
}
