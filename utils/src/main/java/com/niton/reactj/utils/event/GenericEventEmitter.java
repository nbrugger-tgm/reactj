package com.niton.reactj.utils.event;


import com.niton.reactj.api.event.CustomEventEmitter;

public class GenericEventEmitter extends CustomEventEmitter<Void, GenericListener> {
	@Override
	protected void call(GenericListener listener, Void event) {
		listener.onAction();
	}

	public void fire() {
		super.fire(null);
	}
}
