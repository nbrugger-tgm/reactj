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

	public ConditionRunnableBuilder when(Condition condition) {
		return new ConditionRunnableBuilder(new ConditionalRunnable(condition, runnable), rootBuilder);
	}

	public RunnableCallBuilder and(Runnable runnable) {
		RunnableGroup group = (RunnableGroup) this.runnable;
		group.add(runnable);
		return new RunnableCallBuilder(group, rootBuilder);
	}
}
