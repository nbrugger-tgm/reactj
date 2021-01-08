package com.niton.reactj.mvc;

/**
 * An general applicable interface to register listeners
 * @param <E> the type of event this listener will react to
 */
public interface Listener<E>{
	public void onAction(E event);
}
