package com.niton.reactj.core.react;

import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.core.annotation.Unreactive;
import com.niton.reactj.core.util.ReactiveReflectorUtil;
import com.niton.reactj.utils.event.GenericEventEmitter;

import java.io.Serializable;
import java.util.Map;

public class ReactiveWrapper<T> implements Reactable, Serializable {
	private final GenericEventEmitter reactEvent = new GenericEventEmitter();
	@Unreactive
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

	@Override
	public void set(String property, Object value) {
		ReactiveReflectorUtil.updateField(wrappedObject, property, value);
	}

	@Override
	public Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(wrappedObject);
	}

	public T getObject() {
		return wrappedObject;
	}
}
