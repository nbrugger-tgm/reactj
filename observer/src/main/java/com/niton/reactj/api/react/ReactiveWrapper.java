package com.niton.reactj.api.react;

import com.niton.reactj.api.observer.Reactable;
import com.niton.reactj.utils.event.GenericEventEmitter;

import java.io.Serializable;

public class ReactiveWrapper<T> implements Reactable, Serializable {
	private final GenericEventEmitter reactEvent = new GenericEventEmitter();
	private final T                   wrappedObject;

	/**
	 * Creates a Reactive Object that forwards calls to the given object
	 *
	 * @param obj the object to forward calls to
	 */
	public ReactiveWrapper(T obj) {
		wrappedObject = obj;
	}

	@Override
	public GenericEventEmitter reactEvent() {
		return reactEvent;
	}

	public T getObject() {
		return wrappedObject;
	}
}
