package com.niton.reactj;

import com.niton.reactj.exceptions.ReactiveException;

import java.util.Map;

/**
 * <b>This interface does not adds any functionality!</b><br><br>
 *
 * This interface should <b>only</b> be used when the class is constructed as proxy {@link ReactiveProxy#create(Class, Object...)}<br><br>
 *
 * "implementing" this interface removes the need to use {@code ReactiveProxy<MyDataType>} as type definition
 */
public interface ProxySubject extends Reactable {
	/**
	 * After binding this object will report changes to the given observer
	 * @param observer the observer to bind to this object
	 */
	default void bind(Observer<?> observer){}

	/**
	 * Get a map of all properties and their values.<br>
	 *     {@code Map<NameOfField,ValueOfField>} The name should be influenced by {@code @Reactive("name")}
	 * @return the state of the object
	 */
	default Map<String, Object> getState(){return null;}

	/**
	 * Unbinds the object from the observer. By doing this the observer will not be notified about changes anymore
	 * @param observer the observer to unbind
	 */
	default void unbind(Observer<?> observer){}

	/**
	 * Report a change in the state of the object (shoul be called after every setter and mutating method).
	 * <br>
	 *     This should notify all bound Observers
	 */
	default void react(){}

	/**
	 * React to a certain property
	 * @param property the name of the property to react to
	 * @param value the new value
	 */
	default void react(String property, Object value){}

	/**
	 * Updates a field in this object with the respective name (sentivie to @Reactive)
	 * @param property the name of the property to set (@Reactive respected)
	 * @param value the value to change to
	 * @throws Exception if anything goes wrong
	 */
	default void set(String property, Object value) throws Exception{}


	/**
	 * Unbind all Observers.
	 * @see #unbind(Observer)
	 */
	default void unbindAll(){}
}
