package com.niton.reactj.utils.event;


import com.niton.reactj.api.event.CustomEventEmitter;

public class GenericEventEmitter extends CustomEventEmitter<Object, GenericListener> {
	@Override
	protected void call(GenericListener listener, Object event) {
		listener.onAction();
	}

	public void fire() {
		super.fire(null);
	}
}
