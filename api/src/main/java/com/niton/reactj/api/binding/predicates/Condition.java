package com.niton.reactj.api.binding.predicates;

import java.util.function.Predicate;

/**
 * A Condition that results in either true or false. Useful to determine if an action should be
 * executed or not
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
     * Creates a new condition that uses OR to check if either condition is true
     *
     * @param condition the condition to "or" this condition with
     *
     * @return the new condition combining this and the given condition
     */
    default Condition or(Condition condition) {
        return () -> check() || condition.check();
    }

    /**
     * @return result of the condition check
     */
    boolean check();

    /**
     * Creates <i>a new condition</i> that uses AND to check if both conditions are true
     *
     * @param condition the condition to "and" this condition with
     *
     * @return the new condition combining this and the given condition
     */
    default Condition and(Condition condition) {
        return () -> check() && condition.check();
    }

    /**
     * Creates a new condition that is the inverse of this condition
     *
     * @return the new condition
     */
    default Condition not() {
        return () -> !check();
    }

    /**
     * Creates a predicate out of the condition. The predicate will ignore the value
     *
     * @return this as a predicate
     */
    default Predicate<Object> toPredicate() {
        return o -> this.check();
    }
}
