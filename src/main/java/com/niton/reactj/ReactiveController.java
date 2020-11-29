package com.niton.reactj;

import com.niton.reactj.exceptions.ReactiveException;
import com.niton.reactj.special.ListActions;
import com.niton.reactj.special.ReactiveList;

import java.util.*;

public final class ReactiveController<C> {

	private final Map<String, Object>                               valueCache      = new HashMap<>();
	private final Map<String, List<ReactiveBinder.Binding<?, ?>>>   displayBindings = new HashMap<>();
	private final Map<String, List<ReactiveBinder.BiBinding<?, ?>>> editBindings    = new HashMap<>();
	private       Reactable                                         model;
	private       boolean                                           blockReactions  = false;

	public ReactiveController(ReactiveComponent<C> view, C customController) {
		//Maybe in the future it is needed to ass the view as field
		ReactiveBinder binder = new ReactiveBinder(this::updateModel,
		                                           displayBindings,
		                                           editBindings);
		view.createBindings(binder);
		view.createAnnotatedBindings(binder);
		view.registerListeners(customController);
	}

	private void updateModel(EventObject actionEvent) throws Throwable {
		if (blockReactions) {
			return;
		}
		Map<String, Object> changed = findUiChanges();

		if (changed.size() > 0) {
			model.set(changed);
			model.react();
			valueCache.putAll(changed);
		}
	}

	/**
	 * Searches for things that changed in the UI
	 *
	 * @return the changes as map (key: changed property, value: new value)
	 */
	private Map<String, Object> findUiChanges() {
		Map<String, Object> changed = new HashMap<>();
		Map<String, Object> state   = model.getState();
		for (Map.Entry<String, Object> field : state.entrySet()) {
			if (!editBindings.containsKey(field.getKey())) {
				continue;
			}
			findBindingChanges(changed, field.getKey(), field.getValue());
		}
		return changed;
	}

	/**
	 * Finds changes for a specific field among many bindings (but only the first one is accepted)
	 *
	 * @param changed  the map to put the change into (if found)
	 * @param field    the name of the field/property
	 * @param oldValue the value to compare against to find a change
	 */
	private void findBindingChanges(
			Map<String, Object> changed,
			String field,
			Object oldValue
	) {
		List<ReactiveBinder.BiBinding<?, ?>> editBind = editBindings.get(field);
		for (ReactiveBinder.BiBinding<?, ?> biBinding : editBind) {
			Object bindingVal = biBinding.getModelConverted();
			if (!Objects.equals(bindingVal, oldValue)) {
				changed.put(field, bindingVal);
				break;
			}
		}
	}

	/**
	 * Bind the model to the UI
	 *
	 * @param model the model to display for this controller (its UI)
	 */
	public void bind(Reactable model) {
		model.bind(this);
		this.model = model;
		if (model instanceof ReactiveList) {
			model.react(ListActions.INIT.id(), model);
		} else {
			modelChanged();
		}
	}

	/**
	 * Updates the UI to the new state of the model
	 */
	public void modelChanged() {
		Map<String, Object> changed = new HashMap<>();
		getChanges(changed);
		modelChanged(changed);
	}

	/**
	 * Updates the UI to the given values
	 *
	 * @param changed the values that changed and will be changed on the UI
	 */
	public void modelChanged(Map<String, Object> changed) {
		for (Map.Entry<String, Object> stringObjectEntry : changed.entrySet()) {
			updateView(stringObjectEntry.getKey(), stringObjectEntry.getValue());
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
	 * Sends a signal to all bindings with the regarding key
	 *
	 * @param key   the key to react to (most likely the name of the changed property)
	 * @param value the value the event carries
	 */
	private void updateView(final String key, final Object value) {
		List<ReactiveBinder.Binding<?, ?>> bindings = displayBindings.get(key);
		if (bindings == null || bindings.size() == 0) {
			return;
		}
		blockReactions = true;
		bindings.forEach(e -> updateBinding(key, value, e));
		blockReactions = false;
	}

	private void updateBinding(String key, Object value, ReactiveBinder.Binding<?, ?> binding) {
		Object converted;
		try {
			converted = binding.getToDisplayConverter().convert(value);
		} catch (ClassCastException ex) {
			throw badConverterException(key, value.getClass());
		}

		if (binding instanceof ReactiveBinder.BiBinding) {
			//ignore change when the value is already present
			Object present = ((ReactiveBinder.BiBinding<?, ?>) binding).getReciver().get();
			if (present.equals(value)) {
				return;
			}
		}
		try {
			binding.getDisplay().display(converted);
		} catch (ClassCastException ex) {
			throw bindingException(key, value, converted, ex);
		}
	}

	private ReactiveException bindingException(String key,
	                                           Object value,
	                                           Object converted,
	                                           ClassCastException ex) {
		Class<?> original      = value.getClass();
		Class<?> convertedType = converted.getClass();

		ReactiveException exception;
		if (convertedType.equals(original)) {
			exception = badBindingTarget(key, original);
		} else {
			exception = badConverterBindingTarget(key, original, convertedType);
		}
		exception.initCause(ex);
		return exception;
	}

	private ReactiveException badConverterBindingTarget(String key,
	                                                    Class<?> original,
	                                                    Class<?> convertedType) {
		return new ReactiveException(String.format(
				"Bad binding for \"%s\". Target function doesnt accepts converted (from %s to %s)",
				key,
				original.getTypeName(),
				convertedType.getTypeName()));
	}

	private ReactiveException badBindingTarget(String key, Class<?> original) {
		return new ReactiveException(String.format(
				"Bad binding for \"%s\". Target function doesnt accept type %s",
				key,
				original.getTypeName()));
	}

	private ReactiveException badConverterException(String property, Class<?> type) {
		return new ReactiveException(String.format(
				"Bad converter. A converter for \"%s\" doesnt accepts type %s",
				property,
				type.getSimpleName()));
	}

	public Reactable getModel() {
		return model;
	}
}
