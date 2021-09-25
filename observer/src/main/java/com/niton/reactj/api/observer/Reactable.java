package com.niton.reactj.api.observer;

import com.niton.reactj.utils.event.GenericEventEmitter;

/**
 * This interface enables objects to be reactive.<br>
 * implementing this interface makes it able to bind it to a UI
 */
public interface Reactable {


	/**
	 * Report a change in the state of the object (shoul be called after every setter and mutating method).
	 * <br>
	 * This should notify all bound Observers
	 */
	default void react() {
		reactEvent().fire();
	}

	GenericEventEmitter reactEvent();
}
