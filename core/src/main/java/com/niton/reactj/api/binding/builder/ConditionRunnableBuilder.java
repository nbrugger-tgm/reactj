package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.predicates.CombiningCondition;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.predicates.HasCondition;

public class ConditionRunnableBuilder<T extends HasCondition & Runnable> extends BaseRunnableBuilder<T> {

	public ConditionRunnableBuilder(T runnable, BindingBuilder rootBuilder) {
		super(runnable, rootBuilder);
	}

	/**
	 * execute if the previous condition or this condition applies
	 */
	public ConditionRunnableBuilder<T> or(Condition condition) {
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
	public ConditionRunnableBuilder<T> and(Condition condition) {
		CombiningCondition.And and = new CombiningCondition.And(
				runnable.getCondition(),
				condition
		);
		runnable.setCondition(and);
		return this;
	}
}
