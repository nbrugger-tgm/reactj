package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.predicates.Condition;

/**
 * A DSL node used to modify an existing runnable.
 */
public interface RunnableDsl extends ListenerDsl, ConditionalDsl {
    /**
     * Makes the given runnable execute together with the previous defined runnables/bindings.
     * All Conditions (when/or/on) also apply to this runnable. The order of execution is not
     * guaranteed. This might be executed the first but also maybe the last, or something in
     * between.
     *
     * @param runnable the runnable to also call
     */
    RunnableDsl and(Runnable runnable);


    @Override
    ConditionalRunnableDsl when(Condition condition);
}
