package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.predicates.CombiningCondition;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.predicates.HasCondition;
import com.niton.reactj.api.binding.runnable.ConditionalRunnable;

public class ConditionRunnableBuilder extends BaseRunnableBuilder {

	public <T extends HasCondition & Runnable> ConditionRunnableBuilder(T runnable, BindingBuilder rootBuilder) {
		super(runnable, rootBuilder);
	}

	public ConditionRunnableBuilder or(Condition condition) {
		ConditionalRunnable runnable = (ConditionalRunnable) this.runnable;
		CombiningCondition.Or or = new CombiningCondition.Or(
				runnable.getCondition(),
				condition
		);
		runnable.setCondition(or);
		return this;
	}


	public ConditionRunnableBuilder and(Condition condition) {
		ConditionalRunnable runnable = (ConditionalRunnable) this.runnable;
		CombiningCondition.And and = new CombiningCondition.And(
				runnable.getCondition(),
				condition
		);
		runnable.setCondition(and);
		return this;
	}
}
