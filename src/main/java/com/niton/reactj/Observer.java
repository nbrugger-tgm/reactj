package com.niton.reactj;

import com.niton.reactj.special.ListActions;
import com.niton.reactj.special.ReactiveList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Observer<M extends Reactable> {
	private final Map<String, Object>                               valueCache      = new HashMap<>();
	public abstract void onChange(String property,Object value);
	private       M                                         model;

	public M getModel() {
		return model;
	}

	protected Map<String, Object> getValueCache() {
		return valueCache;
	}


	protected void updateCache(Map<String, Object> changed) {
		valueCache.putAll(changed);
	}
	/**
	 * Updates the UI to the new state of the model
	 */
	public void update() {
		Map<String, Object> changed = new HashMap<>();
		getChanges(changed);
		update(changed);
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

	private void getChanges(Map<String, Object> changed) {
		Map<String, Object> state = model.getState();
		for (String property : state.keySet()) {
			detectChange(changed, property, state.get(property));
		}
	}


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
		if(model != null)
			model.unbind(this);
		model.bind(this);
		this.model = model;
		if (model instanceof ReactiveList) {
			model.react(ListActions.INIT.id(), model);
		} else {
			update();
		}
	}

}
