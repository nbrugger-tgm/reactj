package com.niton.reactj.api.binding.predicates;

@FunctionalInterface
public interface Condition {
	Condition NO  = () -> false;
	Condition YES = () -> true;

	boolean check();
}
