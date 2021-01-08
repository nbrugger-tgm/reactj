package com.niton.reactj;

import com.niton.reactj.exceptions.ReactiveException;

import java.util.Map;

/**
 * This interface enables objects to be reactive.<br>
 * implementing this interface makes it able to bind it to an UI
 */
public interface Reactable {
	/**
	 * After binding this object will report changes to the given observer
	 * @param observer the observer to bind to this object
	 */
	void bind(Observer<?> observer);

	/**
	 * Get a map of all properties and their values.<br>
	 *     {@code Map<NameOfField,ValueOfField>} The name should be influenced by {@code @Reactive("name")}
	 * @return the state of the object
	 */
	Map<String, Object> getState();

	/**
	 * Unbinds the object from the observer. By doing this the observer will not be notified about changes anymore
	 * @param observer the observer to unbind
	 */
	void unbind(Observer<?> observer);

	/**
	 * Report a change in the state of the object (shoul be called after every setter and mutating method).
	 * <br>
	 *     This should notify all bound Observers
	 */
	void react();

	/**
	 * React to a certain property
	 * @param property the name of the property to react to
	 * @param value the new value
	 */
	void react(String property, Object value);

	/**
	 * Updates a field in this object with the respective name (sentivie to @Reactive)
	 * @param property the name of the property to set (@Reactive respected)
	 * @param value the value to change to
	 * @throws Exception if anything goes wrong
	 */
	void set(String property, Object value) throws Exception;

	/**
	 * Performs multiple {@link #set(String, Object)} operations. One for every Map Entry
	 * @param changed the Map containing all changes
	 */
	default void set(Map<String, Object> changed) {
		for (Map.Entry<String, Object> change : changed.entrySet()) {
			try{
				set(change.getKey(), change.getValue());
			}catch (Exception e){
				throw new ReactiveException("Set("+change.getKey()+", "+change.getValue()+") failed",e);
			}
		}
	}

	/**
	 * Unbind all Observers.
	 * @see #unbind(Observer)
	 */
	void unbindAll();
}
