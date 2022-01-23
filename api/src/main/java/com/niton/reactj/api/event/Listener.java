package com.niton.reactj.api.event;

/**
 * A general applicable interface to register listeners
 *
 * @param <E> the type of event this listener will react to
 */
public interface Listener<E> {
    /**
     * The method called when the event occurs
     *
     * @param event the payload of the event
     */
    void onAction(E event);
}
