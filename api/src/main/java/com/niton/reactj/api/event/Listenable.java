package com.niton.reactj.api.event;

/**
 * Implementing this interface makes it possible to listen to instances of the class
 */
@FunctionalInterface
public interface Listenable {
    /**
     * Adds the runnable as some sort of listener to an event specified by the implementation.
     *
     * @param listener the runnable to be added as a listener
     */
    void listen(Runnable listener);
}
