package com.niton.reactj;

import java.util.Map;

/**
 * This interface enables objects to be reactive.<br>
 * implementing this interface makes it able to bind it to an UI
 */
public interface Reactable {
	void bind(Observer<?> c);

	Map<String, Object> getState();

	void unbind(Observer<?> c);

	void react();

	void react(String property, Object value);

	void set(String property, Object value) throws Throwable;

	default void set(Map<String, Object> changed) throws Throwable {
		for (Map.Entry<String, Object> change : changed.entrySet()) {
			set(change.getKey(), change.getValue());
		}
	}
}
