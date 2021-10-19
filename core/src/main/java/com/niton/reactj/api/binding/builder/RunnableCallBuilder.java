package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.runnable.ConditionalRunnable;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

public class RunnableCallBuilder extends BaseRunnableBuilder {


	public RunnableCallBuilder(Runnable r, BindingBuilder rootBuilder) {
		super(new RunnableGroup(r), rootBuilder);
	}

	public RunnableCallBuilder(RunnableGroup r, BindingBuilder rootBuilder) {
		super(r, rootBuilder);
	}

	/**
	 * Only execute the previous statement when the condition applies (-> returns true)
	 *
	 * @param condition the condition to check before execution
	 */
	public ConditionRunnableBuilder when(Condition condition) {
		return new ConditionRunnableBuilder(new ConditionalRunnable(condition, runnable), rootBuilder);
	}


	/**
	 * Call this runnable together with previous ones forming a group.
	 * <p>
	 * conditions and predicates only apply to a group. A group is seperated from other groups using {@link #andAlso()}
	 */
	public RunnableCallBuilder and(Runnable runnable) {
		RunnableGroup group = (RunnableGroup) this.runnable;
		group.add(runnable);
		return new RunnableCallBuilder(group, rootBuilder);
	}
}