package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;

public interface ConditionalRunnableDsl extends ConditionDsl, ListenerDsl {
	ConditionalRunnableDsl or(Condition condition);

	ConditionalRunnableDsl and(Condition condition);

	Runnable build();
}
