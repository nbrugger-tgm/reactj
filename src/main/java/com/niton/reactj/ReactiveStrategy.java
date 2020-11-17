package com.niton.reactj;

import java.util.Arrays;

public enum ReactiveStrategy {
	REACT_ON_SETTER((a, b) -> a.startsWith("set")),
	REACT_ON_ALL((a, b) -> true),
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
