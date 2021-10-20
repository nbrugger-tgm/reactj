package com.niton.reactj.api.event;


/**
 * An event emitter that emits events without data
 */
public class GenericEventEmitter extends CustomEventEmitter<Object, GenericListener> {
	@Override
	protected void call(GenericListener listener, Object event) {
		listener.onAction();
	}

	public void fire() {
		super.fire(null);
	}
}
