package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;

public interface RunnableDsl extends ListenerDsl, ConditionalDsl {
	RunnableDsl and(Runnable runnable);

	ConditionalRunnableDsl when(Condition condition);
}
