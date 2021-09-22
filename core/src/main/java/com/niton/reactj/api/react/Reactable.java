package com.niton.reactj.api.react;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.event.GenericEventManager;

import java.util.Map;

import static java.lang.String.format;

/**
 * This interface enables objects to be reactive.<br>
 * implementing this interface makes it able to bind it to a UI
 */
public interface Reactable {

	/**
	 * Get a map of all properties and their values.<br>
	 * {@code Map<NameOfField,ValueOfField>} The name should be influenced by {@code @Reactive("name")}
	 *
	 * @return the state of the object
	 */
	Map<String, Object> getState();

	/**
	 * Report a change in the state of the object (shoul be called after every setter and mutating method).
	 * <br>
	 * This should notify all bound Observers
	 */
	default void react() {
		reactEvent().fire();
	}

	GenericEventManager reactEvent();

	/**
	 * Performs multiple {@link #set(String, Object)} operations. One for every Map Entry
	 *
	 * @param changed the Map containing all changes
	 */
	default void set(Map<String, Object> changed) {
		for (Map.Entry<String, Object> change : changed.entrySet()) {
			try {
				set(change.getKey(), change.getValue());
			} catch (Exception e) {
				throw new ReactiveException(format("Set(%s, %s) failed", change.getKey(), change.getValue()), e);
			}
		}
	}

	/**
	 * Updates a field in this object with the respective name (sentivie to @Reactive)
	 *
	 * @param property the name of the property to set (@Reactive respected)
	 * @param value    the value to change to
	 * @throws Exception if anything goes wrong
	 */
	void set(String property, Object value);
}
