package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;

/**
 * A node to add 'and' and 'or' boolean logic to the previously defined condition.
 */
public interface ConditionalRunnableDsl extends ConditionDsl, ListenerDsl {
    @Override
    ConditionalRunnableDsl or(Condition condition);

    @Override
    ConditionalRunnableDsl and(Condition condition);

    /**
     * @return the runnable that was crafted by the DSL so far
     *
     * @apiNote keep in mind that this runnable
     * already contains the defined conditions, so it might not execute if you perform
     * {@link Runnable#run()} on the result.
     */
    Runnable build();
}
