package com.niton.reactj.api.binding.builder.conditional;

import com.niton.reactj.api.binding.predicates.CombiningCondition;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.predicates.HasCondition;
import com.niton.reactj.implementation.binding.BaseRunnableBuilder;
import com.niton.reactj.implementation.binding.CallBuilder;

public class ConditionalRunnableBuilder<T extends HasCondition & Runnable> extends
                                                                           BaseRunnableBuilder<T> {

	public ConditionalRunnableBuilder(T runnable, CallBuilder rootBuilder) {
		super(runnable, rootBuilder);
	}

	/**
	 * execute if the previous condition or this condition applies
	 */
	public ConditionalRunnableBuilder<T> or(Condition condition) {
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
	public ConditionalRunnableBuilder<T> and(Condition condition) {
		CombiningCondition.And and = new CombiningCondition.And(
				runnable.getCondition(),
				condition
		);
		runnable.setCondition(and);
		return this;
	}
}
