package com.niton.reactj.mvc;

/**
 * An easy and fast to use implementation of {@link CustomEventManager}.
 *
 * @param <E> The class to use for events
 */
public class EventManager<E> extends CustomEventManager<E, Listener<E>> {

	@Override
	protected void call(Listener<E> listener, E event) {
		listener.onAction(event);
	}
}
