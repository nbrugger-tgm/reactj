package com.niton.reactj;

import com.niton.reactj.special.ListActions;
import com.niton.reactj.special.ReactiveList;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An Observer is used to observe changes in the state of an Object,
 * eg. reports changes to the properties
 *
 * @param <M> The Model class to observe
 */
public abstract class Observer<M extends Reactable> {
	private final     Map<String, Object> valueCache = new ConcurrentHashMap<>();
	private transient M                   model;

	/**
	 * Listender-like<br>
	 * Called when an property of the bound object changes
	 *
	 * @param property the name of the changed property as a String
	 * @param value    the new assigned value
	 */
	public abstract void onChange(String property, Object value);

	public M getModel() {
		return model;
	}

	protected Map<String, Object> getValueCache() {
		return valueCache;
	}


	/**
	 * Updates the cache with the pairs in the map
	 *
	 * @param changed the map to put into the cache
	 */
	protected void updateCache(Map<String, Object> changed) {
		valueCache.putAll(changed);
	}

	/**
	 * Updates the UI to the new state of the model
	 */
	public void update() {
		update(getChanges());
	}

	/**
	 * Updates the UI to the given values
	 *
	 * @param changed the values that changed and will be changed on the UI
	 */
	public void update(Map<String, Object> changed) {
		for (Map.Entry<String, Object> stringObjectEntry : changed.entrySet()) {
			onChange(stringObjectEntry.getKey(), stringObjectEntry.getValue());
		}
	}

	/**
	 * Calculates every property that changed since the last cache update
	 *
	 * @return the map of all properties that changed
	 */
	private Map<String, Object> getChanges() {
		final Map<String, Object> changed = new ConcurrentHashMap<>();
		final Map<String, Object> state   = model.getState();
		for (String property : state.keySet()) {
			detectChange(changed, property, state.get(property));
		}
		return changed;
	}

	/**
	 * Adds the property to the Map if the actual value is different than the one in cache
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


	/**
	 * Bind the model to the UI
	 *
	 * @param model the model to display for this controller (its UI)
	 */
	public void bind(M model) {
		if (model == null) {
			throw new IllegalArgumentException("Cannot bind to null");
		}
		if (this.model != null) {
			this.model.unbind(this);
		}
		model.bind(this);
		this.model = model;
		valueCache.clear();
		valueCache.putAll(model.getState());


		//This is kind of propritary and is subject to change
		if (model instanceof ReactiveList) {
			model.react(ListActions.INIT.id(), model);
		} else {
			update();
		}
	}

}
