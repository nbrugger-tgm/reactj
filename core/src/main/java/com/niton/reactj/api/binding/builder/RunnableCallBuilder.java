package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.runnable.ConditionalRunnable;

public class RunnableCallBuilder extends BaseRunnableBuilder {


	public RunnableCallBuilder(Runnable r, BindingBuilder rootBuilder) {
		super(r, rootBuilder);
	}

	public ConditionRunnableBuilder when(Condition condition) {
		return new ConditionRunnableBuilder(new ConditionalRunnable(condition, runnable), rootBuilder);
	}

}
