package com.niton.reactj.objects.observer;


import com.niton.reactj.api.observer.Observer;
import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.objects.reflect.Reflective;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An Observer is used to observe changes in the state of an Object,
 * e.g. reports changes to the properties
 * <p>Only works with {@link Reactable} classes. There are multiple ways to achieve this.</p>
 */
public class ObjectObserver<M extends Reactable & Reflective> extends
                                                              Observer<PropertyObservation<M>, M> {

	private final Map<String, Object> valueCache = new ConcurrentHashMap<>();

	protected Map<String, Object> getValueCache() {
		return Collections.unmodifiableMap(valueCache);
	}

	/**
	 * Updates the cache with the pairs in the map
	 *
	 * @param changed the map to put into the cache
	 */
	public void updateCache(Map<String, Object> changed) {
		valueCache.putAll(changed);
	}

	@Override
	public void reset() {
		valueCache.clear();
		if (isObservingRebind())
			update();
	}

	/**
	 * Updates the UI to the new state of the model
	 */
	public void update() {
		if (getObserved() != null)
			update(getChanges());
	}

	/**
	 * Reports all changes in the map to all listeners
	 *
	 * @param changed the values that changed and will be changed on the UI
	 */
	public void update(Map<String, Object> changed) {
		for (Map.Entry<String, Object> property : changed.entrySet()) {
			PropertyObservation<M> change = new PropertyObservation<M>(
					property.getKey(),
					property.getValue(),
					getObserved()
			);
			fireObservation(change);
		}
	}

	/**
	 * Calculates every property that changed since the last cache update
	 *
	 * @return the map of all properties that changed
	 */
	private Map<String, Object> getChanges() {
		final Map<String, Object> changed = new ConcurrentHashMap<>();
		final Map<String, Object> state   = observedObject.getState();
		for (Map.Entry<String, Object> property : state.entrySet()) {
			detectChange(changed, property.getKey(), property.getValue());
		}
		return changed;
	}

	/**
	 * Adds the property to the Map if the actual value is different from the one in cache
	 *
	 * @param changed      the map to add the change to if necessary
	 * @param property     the name of the property to check
	 * @param currentValue the value to compare to the cache
	 */
	private void detectChange(Map<String, Object> changed, String property, Object currentValue) {
		Object oldValue = valueCache.get(property);
		if (!Objects.equals(currentValue, oldValue)) {
			valueCache.put(property, currentValue);
			changed.put(property, currentValue);
		}
	}

}
