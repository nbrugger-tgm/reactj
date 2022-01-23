package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;

/**
 * Not being a DSL node itself it can be used to define a DSL node.
 *
 * <p>
 * Implement this interface to make it possible for the node to take make its object
 * conditional, which means that it will only be executed if the condition is met.
 * </p>
 */
public interface ConditionalDsl {
    /**
     * Only execute the previous crafted statement if the condition is met.
     * <i>
     * Keep in mind that this does not executes <b>yet</b> but at a later point which is defined by
     * the
     * chain. So the condition as well as the previous statement are executed later.
     * </i>
     *
     * @param condition the condition to check before running the previous statement
     */
    ConditionDsl when(Condition condition);
}
