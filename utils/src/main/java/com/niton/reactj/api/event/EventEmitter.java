package com.niton.reactj.api.event;

/**
 * An easy and fast to use implementation of {@link CustomEventEmitter}.
 *
 * @param <E> The class to use for events
 */
public class EventEmitter<E> extends CustomEventEmitter<E, Listener<E>> {

	@Override
	protected void call(Listener<E> listener, E event) {
		listener.onAction(event);
	}
}
