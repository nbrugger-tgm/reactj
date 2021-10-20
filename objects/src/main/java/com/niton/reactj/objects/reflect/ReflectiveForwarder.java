package com.niton.reactj.objects.reflect;

import java.util.Map;

public interface ReflectiveForwarder extends Reflective {

	@Override
	default void set(String property, Object value) {
		getReflectiveTarget().set(property, value);
	}

	Reflective getReflectiveTarget();

	@Override
	default Map<String, Object> getState() {
		return getReflectiveTarget().getState();
	}
}
