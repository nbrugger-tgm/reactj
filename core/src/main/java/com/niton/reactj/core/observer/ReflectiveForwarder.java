package com.niton.reactj.core.observer;

import java.util.Map;

public interface ReflectiveForwarder extends Reflective {

	@Override
	public default void set(String property, Object value) {
		getReflectiveTarget().set(property, value);
	}

	abstract Reflective getReflectiveTarget();

	@Override
	public default Map<String, Object> getState() {
		return getReflectiveTarget().getState();
	}
}
