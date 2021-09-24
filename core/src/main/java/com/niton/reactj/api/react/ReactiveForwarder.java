package com.niton.reactj.api.react;

import com.niton.reactj.event.GenericEventManager;

import java.util.Map;

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
	default Map<String, Object> getState() {
		return getReactableTarget().getState();
	}

	@Override
	default void react() {
		getReactableTarget().react();
	}

	@Override
	default GenericEventManager reactEvent() {
		return getReactableTarget().reactEvent();
	}

	Reactable getReactableTarget();

	@Override
	default void set(String property, Object value) {
		getReactableTarget().set(property, value);
	}
}
