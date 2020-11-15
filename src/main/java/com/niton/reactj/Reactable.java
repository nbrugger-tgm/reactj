package com.niton.reactj;

import java.util.Map;

public interface Reactable {
	void bind(ReactiveController<?> c);
	public Map<String,Object> getState();
	void unbind(ReactiveController<?> c);
	void react();

	void set(String property, Object value) throws Throwable;
}
