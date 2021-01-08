package com.niton.reactj.mvc;

public class EventManager<E> extends CustomEventManager<E,Listener<E>>{

	@Override
	protected void call(Listener<E> eListener, E event) {
		eListener.onAction(event);
	}
}
