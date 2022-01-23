package com.niton.reactj.api.event;

import com.niton.reactj.api.binding.Listenable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An event manager is responsible for committing events and handle listeners
 *
 * @param <E> the type to be used for events
 * @param <L> the type to be used for listeners
 */
public abstract class CustomEventEmitter<E, L> implements Serializable, Listenable {
    /**
     * The list that will be called when an event is emitted
     */
    private final List<L> listeners = new ArrayList<>(1);

    /**
     * Adds the listeners to the list of listeners that will be called when the event is fired
     *
     * @param listener the listener to add, null is forbidden
     */
    public void addListener(L listener) {
        listen(listener);
    }

    /**
     * @see #addListener(Object)
     */
    public void listen(L listener) {
        if (listener == null)
            throw new IllegalArgumentException();
        listeners.add(listener);
    }

    /**
     * Removes the listener from the list of listeners, if it is present
     */
    public void stopListening(L listener) {
        listeners.remove(listener);
    }

    /**
     * Removes all listeners that were listening to this emitter
     */
    public void removeListeners() {
        listeners.clear();
    }

    /**
     * An unmodifiable list of the registered listeners
     */
    public List<L> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    /**
     * Calls all listeners with the given event
     *
     * @param event the event to carry over to the listeners
     */
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

    /**
     * @return the number of listeners present
     */
    public int listenerCount() {
        return listeners.size();
    }
}
