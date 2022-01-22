package com.niton.reactj.objects.dsl;

import com.niton.reactj.api.binding.dsl.BindingDsl;
import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

public interface ObjectBindingDsl<T> extends ObjectRunnableDsl, BindingDsl<T> {
	@Override
	ObjectConditionalBindingDsl<T> when(Predicate<? super T> predicate);

	@Override
	ObjectConditionalBindingDsl<T> when(Condition condition);
}
