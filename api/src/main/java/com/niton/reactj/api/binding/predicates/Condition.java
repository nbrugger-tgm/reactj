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

    default Condition or(Condition condition) {
        return () -> check() || condition.check();
    }

    /**
     * @return result of the condition check
     */
    boolean check();

    default Condition and(Condition condition) {
        return () -> check() && condition.check();
    }

    default Condition not() {
        return () -> !check();
    }

    default Predicate<Object> toPredicate() {
        return o -> this.check();
    }
}
