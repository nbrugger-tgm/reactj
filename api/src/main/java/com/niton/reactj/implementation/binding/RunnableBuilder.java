package com.niton.reactj.implementation.binding;

import com.niton.reactj.api.binding.builder.conditional.ConditionalRunnableBuilder;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.runnable.ConditionalRunnable;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

public class RunnableBuilder extends BaseRunnableBuilder {


	public RunnableBuilder(Runnable r, CallBuilder rootBuilder) {
		super(new RunnableGroup(r), rootBuilder);
	}

	public RunnableBuilder(RunnableGroup r, CallBuilder rootBuilder) {
		super(r, rootBuilder);
	}

	/**
	 * Only execute the previous statement when the condition applies (returns true)
	 *
	 * @param condition the condition to check before execution
	 */
	public ConditionalRunnableBuilder when(Condition condition) {
		return new ConditionalRunnableBuilder(
				new ConditionalRunnable(condition, runnable),
				rootBuilder
		);
	}


	/**
	 * Call this runnable together with previous ones forming a group.
	 * <p>
	 * conditions and predicates only apply to a group. A group is seperated from other groups using
	 * {@link #andAlso()}
	 */
	public RunnableBuilder and(Runnable runnable) {
		RunnableGroup group = (RunnableGroup) this.runnable;
		group.add(runnable);
		return new RunnableBuilder(group, rootBuilder);
	}
}
