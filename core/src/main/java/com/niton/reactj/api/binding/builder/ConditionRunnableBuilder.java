package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.predicates.CombiningCondition;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.predicates.HasCondition;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

public class ConditionRunnableBuilder extends BaseRunnableBuilder {

	public <T extends HasCondition & Runnable> ConditionRunnableBuilder(T runnable, BindingBuilder rootBuilder) {
		super(runnable, rootBuilder);
	}

	public <T extends RunnableGroup & HasCondition> ConditionRunnableBuilder(T runnable, BindingBuilder rootBuilder) {
		super(runnable, rootBuilder);
	}

	/**
	 * execute if the previous condition or this condition applies
	 */
	public ConditionRunnableBuilder or(Condition condition) {
		HasCondition runnable = (HasCondition) this.runnable;
		CombiningCondition.Or or = new CombiningCondition.Or(
				runnable.getCondition(),
				condition
		);
		runnable.setCondition(or);
		return this;
	}


	/**
	 * execute if the previous condition and this condition applies
	 */
	public ConditionRunnableBuilder and(Condition condition) {
		HasCondition runnable = (HasCondition) this.runnable;
		CombiningCondition.And and = new CombiningCondition.And(
				runnable.getCondition(),
				condition
		);
		runnable.setCondition(and);
		return this;
	}
}
