package com.niton.reactj.api.react;

import java.util.Arrays;

/**
 * Defines methods that should be reacted to
 */
public enum ReactiveStrategy {
	/**
	 * Only react to methods that start with "set"
	 */
	REACT_ON_SETTER((a, b) -> a.startsWith("set")),
	/**
	 * Reacts to all method calls
	 */
	REACT_ON_ALL((a, b) -> true),
	/**
	 * Reacts to a list of custom definable method names
	 */
	REACT_ON_CUSTOM((a, b) -> Arrays.asList(b).contains(a));
	private final ReactionDecider decider;

	/**
	 * Decides wether to react to a method with a certain name or not
	 */
	@FunctionalInterface
	private interface ReactionDecider {
		boolean decideReaction(String methodName, String... acceptors);
	}

	ReactiveStrategy(ReactionDecider decider) {
		this.decider = decider;
	}

	/**
	 * Returns true if the given method name is covered by the strategy.<br>
	 *
	 * @param name    the name of the method to check
	 * @param reactTo only used for REACT_ON_CUSTOM elswhile can be empty or null
	 *
	 * @return true if this strategy reacts to the given method name
	 */
	public boolean reactTo(String name, String... reactTo) {
		return decider.decideReaction(name, reactTo);
	}
}
