package com.niton.reactj.api.binding.predicates;

import java.util.function.Predicate;

/**
 * Same as {@link HasPredicate} but using {@link Condition}s instead of {@link Predicate}s
 */
public interface HasCondition {
	/**
	 * {@link HasPredicate#getPredicate()}
	 */
	Condition getCondition();

	/**
	 * {@link HasPredicate#setPredicate(Predicate)}
	 */
	void setCondition(Condition condition);
}
