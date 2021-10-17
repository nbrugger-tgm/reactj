package com.niton.reactj.api.binding.predicates;

/**
 * A Condition that results in either true or false. Useful to determine if an action should be executed or not
 */
@FunctionalInterface
public interface Condition {
	/**
	 * This condition is always false
	 */
	Condition NO  = () -> false;
	/**
	 * Always true
	 */
	Condition YES = () -> true;

	/**
	 * @return result of the condition check
	 */
	boolean check();
}
