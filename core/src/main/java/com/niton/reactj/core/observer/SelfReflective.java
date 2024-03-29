package com.niton.reactj.core.observer;

import com.niton.reactj.core.util.ReactiveReflectorUtil;

import java.util.Map;

public interface SelfReflective extends Reflective {
	@Override
	default void set(String property, Object value) {
		ReactiveReflectorUtil.updateField(this, property, value);
	}

	@Override
	default Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(this);
	}
}
