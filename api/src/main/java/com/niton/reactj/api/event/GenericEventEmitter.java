package com.niton.reactj.api.event;


/**
 * An event emitter that emits events without data
 */
public class GenericEventEmitter
        extends CustomEventEmitter<Object, Runnable>
        implements Listenable {
    @Override
    protected void call(Runnable listener, Object event) {
        listener.run();
    }

    public void fire() {
        super.fire(null);
    }
}
