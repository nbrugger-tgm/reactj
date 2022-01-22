package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;

public interface ConditionDsl {
	ConditionDsl or(Condition condition);

	ConditionDsl and(Condition condition);
}
