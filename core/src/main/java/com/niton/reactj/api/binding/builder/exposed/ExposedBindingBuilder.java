package com.niton.reactj.api.binding.builder.exposed;

import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

public interface ExposedBindingBuilder<T, R> {
	R when(Condition condition);

	R when(Predicate<T> condition);

}
