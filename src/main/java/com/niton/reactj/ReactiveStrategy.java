package com.niton.reactj;

import java.util.Arrays;

/**
 * This strategies defining the methods that should be reacted to
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

	@FunctionalInterface
	private interface ReactionDecider {
		boolean decideReaction(String methodName, String[] acceptors);
	}

	ReactiveStrategy(ReactionDecider decider) {
		this.decider = decider;
	}

	public boolean reactTo(String name, String[] reactTo) {
		return decider.decideReaction(name, reactTo);
	}
}
