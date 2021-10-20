package com.niton.reactj.objects.reflect;

import com.niton.reactj.core.util.ReactiveReflectorUtil;

import java.util.Map;

public interface ReflectiveWrapper extends Reflective {
	@Override
	default void set(String property, Object value) {
		ReactiveReflectorUtil.updateField(getReflectiveTarget(), property, value);
	}

	Object getReflectiveTarget();

	@Override
	default Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(getReflectiveTarget());
	}
}
