package com.niton.reactj.api.event;

import com.niton.reactj.api.binding.Listenable;

/**
 * An easy and fast to use implementation of {@link CustomEventEmitter}.
 *
 * @param <E> The class to use for events
 */
public class EventEmitter<E>
        extends CustomEventEmitter<E, Listener<? super E>>
        implements Listenable {

    @Override
    protected void call(Listener<? super E> listener, E event) {
        listener.onAction(event);
    }

    public void listen(Runnable listener) {
        super.addListener(o -> listener.run());
    }
}
