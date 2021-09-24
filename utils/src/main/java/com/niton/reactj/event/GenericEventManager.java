package com.niton.reactj.event;


public class GenericEventManager extends CustomEventManager<Void, GenericListener> {
	@Override
	protected void call(GenericListener listener, Void event) {
		listener.onAction();
	}

	public void fire() {
		super.fire(null);
	}
}