package com.niton.reactj.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An event manager is responsible for committing events and handle listeners
 *
 * @param <E> the type to be used for events
 * @param <L> the type to be used for listeners
 */
public abstract class CustomEventManager<E, L> implements Serializable {
	private final List<L> listeners = new ArrayList<>(1);

	public void listen(L listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		listeners.add(listener);
	}

	public void addListener(L listener) {
		listen(listener);
	}

	public void stopListening(L listener) {
		listeners.remove(listener);
	}

	public void removeListeners() {
		listeners.clear();
	}

	public List<L> getListeners() {
		return listeners;
	}

	public void fire(E event) {
		listeners.forEach(l -> call(l, event));
	}

	/**
	 * Call the listener with the given event
	 *
	 * @param listener the listener to call
	 * @param event    the event to carry over to the listener
	 */
	protected abstract void call(L listener, E event);
}
