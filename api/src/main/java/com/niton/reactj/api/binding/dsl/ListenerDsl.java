package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.Listenable;

/**
 * A DSL node that can be used as is or used as extension.
 * <p>
 * Used at the very end of the DSL to define the listener, or in other words define when the
 * crafted actions from the previous DSL nodes will be executed.
 * </p>
 */
public interface ListenerDsl {

    /**
     * Attaches the previous commands/actions to the given {@link Listenable} as listener,
     * so if the {@link Listenable} fires an event, the actions will be executed.
     *
     * @param event the event to make the previously created actions listen to
     */
    MultiListenerDsl on(Listenable event);

    /**
     * @return the runnable created so far by the DSL chain
     */
    Runnable build();
}
