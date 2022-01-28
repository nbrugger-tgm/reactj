package com.niton.reactj.api.react;

import com.niton.reactj.api.event.GenericEventEmitter;

/**
 * This interface enables objects to be reactive.<br>
 * implementing this interface makes it able to bind it to a UI
 */
public interface Reactable {


    /**
     * Report a change in the state of the object (should be called after every setter and mutating
     * method).
     * <br>
     * This should notify all bound Observers
     */
    default void react() {
        reactEvent().fire();
    }

    /**
     * Returns the eventEmitter that is called when the object is changed. By listening to this
     * emitter
     * you will be notified about every state change of the object
     */
    GenericEventEmitter reactEvent();
}
