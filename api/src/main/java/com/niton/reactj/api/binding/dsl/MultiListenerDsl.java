package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.event.Listenable;

/**
 * The very last part of the default DSL. Used to execute the runnable on multiple
 * {@link Listenable}s.
 */
public interface MultiListenerDsl {
    /**
     * Makes the previouly created runnable also execute on the given listenable.
     *
     * @param event the listenable to (also) execute the runnable on
     */
    MultiListenerDsl andOn(Listenable event);
}
