package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;

/**
 * An extension interface for DSL nodes. Extend this interface to add boolean and/or logic to the
 * Node.
 */
public interface ConditionDsl {
    /**
     * Adds a 'or' condition to the previously defined condition.
     *
     * @param condition the condition to 'or' with.
     *                  So the resulting condition will be {@code (prevCondition || condition)}
     */
    ConditionDsl or(Condition condition);

    /**
     * Adds a 'and' condition to the previously defined condition.
     *
     * @param condition the condition to 'and' with.
     *                  So the resulting condition will be {@code (prevCondition && condition)}
     */
    ConditionDsl and(Condition condition);
}
