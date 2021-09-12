package com.niton.reactj.proxy;

import com.niton.reactj.Reactable;
import com.niton.reactj.mvc.GenericEventManager;

import java.util.Map;

/**
 * <b>This interface does not adds any functionality!</b><br><br>
 * <p>
 * This interface should <b>only</b> be used when the class is constructed as proxy {@link
 * ReactiveProxyEngine#create(Class, Object...)}<br><br>
 * <p>
 * "implementing" this interface removes the need to use {@code ReactiveProxy<MyDataType>} as type definition
 */
public interface ProxySubject extends Reactable {

	/**
	 * Get a map of all properties and their values.<br>
	 * {@code Map<NameOfField,ValueOfField>} The name should be influenced by {@code @Reactive("name")}
	 *
	 * @return the state of the object
	 */
	default Map<String, Object> getState() {
		return null;
	}

	@Override
	default GenericEventManager reactEvent() {return null;}

	//default EventManager<ObjectObserver.PropertyObservation> getEventManager(){return null;}

	/**
	 * Updates a field in this object with the respective name (sentivie to @Reactive)
	 *
	 * @param property the name of the property to set (@Reactive respected)
	 * @param value    the value to change to
	 *
	 * @throws Exception if anything goes wrong
	 */
	default void set(String property, Object value) throws Exception {}
}
