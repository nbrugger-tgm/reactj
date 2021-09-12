package com.niton.reactj;

import com.niton.reactj.annotation.Unreactive;
import com.niton.reactj.mvc.GenericEventManager;
import com.niton.reactj.util.ReactiveReflectorUtil;

import java.io.Serializable;
import java.util.Map;

public class ReactiveWrapper<T> implements Reactable, Serializable {
	private final GenericEventManager reactEvent = new GenericEventManager();
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
	public Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(wrappedObject);
	}

	@Override
	public GenericEventManager reactEvent() {
		return reactEvent;
	}

	@Override
	public void set(String property, Object value) throws Exception {
		ReactiveReflectorUtil.updateField(wrappedObject, property, value);
	}

	public T getObject() {
		return wrappedObject;
	}
}
