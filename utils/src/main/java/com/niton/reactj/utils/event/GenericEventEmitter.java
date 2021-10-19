package com.niton.reactj.utils.event;


import com.niton.reactj.api.event.CustomEventEmitter;

/**
 * An event emitter that emits events without data
 */
public class GenericEventEmitter extends CustomEventEmitter<Void, GenericListener> {
	@Override
	protected void call(GenericListener listener, Object event) {
		listener.onAction();
	}

	public void fire() {
		super.fire(null);
	}
}
