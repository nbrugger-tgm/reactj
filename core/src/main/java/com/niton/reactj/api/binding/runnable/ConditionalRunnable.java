package com.niton.reactj.api.binding.runnable;

import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.predicates.HasCondition;

/**
 * A runnable that is only executed in specific circumstances
 */
public class ConditionalRunnable implements Runnable, HasCondition {
	private final Runnable  target;
	/**
	 * A condition that has to result with true in order for the runnable to run
	 */
	private       Condition condition;

	public ConditionalRunnable(Condition condition, Runnable target) {
		if (condition == null)
			throw new IllegalArgumentException("Condition can't be null");
		if (target == null)
			throw new IllegalArgumentException("Target can't be null");
		this.condition = condition;
		this.target    = target;
	}

	public Condition getCondition() {
		return condition;
	}

	@Override
	public void setCondition(Condition condition) {
		if (condition == null)
			throw new IllegalArgumentException("Condition can't be null");
		this.condition = condition;
	}

	/**
	 * runs the underlying runnable when the condition is true
	 */
	@Override
	public void run() {
		if (condition.check())
			target.run();
	}
}
