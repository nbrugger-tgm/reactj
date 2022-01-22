package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;

public interface ConditionalDsl {
	ConditionDsl when(Condition condition);
}
