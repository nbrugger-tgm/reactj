package com.niton.reactj;

import java.util.Map;

/**
 * This interface enables objects to be reactive.<br>
 * implementing this interface makes it able to bind it to an UI
 */
public interface Reactable {
	void bind(ReactiveController<?> c);

	Map<String, Object> getState();

	void unbind(ReactiveController<?> c);

	void react();

	void react(String property, Object value);

	void set(String property, Object value) throws Throwable;
}
