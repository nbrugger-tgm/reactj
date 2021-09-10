package com.niton.reactj.mvc;


public class GenericEventManager extends CustomEventManager<Void,GenericListener> {
	@Override
	protected void call(GenericListener listener, Void event) {
		listener.onAction();
	}
}
