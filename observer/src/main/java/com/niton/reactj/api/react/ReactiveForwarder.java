package com.niton.reactj.api.react;

import com.niton.reactj.api.observer.Reactable;
import com.niton.reactj.utils.event.GenericEventEmitter;

/**
 * This Reactable implementation forwards all calls to a given target.
 * <p>
 * This is useful if there is an implementation you want to use, but you are already extending from a class.
 * Then you can create an instance of your implementation ({@link ReactiveWrapper} for example) and return it from
 * {@link #getReactableTarget()}.
 * This way the object itself is {@link Reactable} <b>and</b> using the right implementation
 * </p>
 */
public interface ReactiveForwarder extends Reactable {
	@Override
	default void react() {
		getReactableTarget().react();
	}

	@Override
	default GenericEventEmitter reactEvent() {
		return getReactableTarget().reactEvent();
	}

	Reactable getReactableTarget();
}
