package com.niton.reactj;

import com.niton.reactj.exceptions.ReactiveException;
import com.niton.reactj.special.ListActions;
import com.niton.reactj.special.ReactiveList;

import java.util.*;

public final class ReactiveController<C,M extends Reactable> extends Observer<M>{

	private final Map<String, List<ReactiveBinder.Binding<?, ?>>>   displayBindings = new HashMap<>();
	private final Map<String, List<ReactiveBinder.BiBinding<?, ?>>> editBindings    = new HashMap<>();
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
			getModel().set(changed);
			getModel().react();
			updateCache(changed);
		}
	}

	/**
	 * Searches for things that changed in the UI
	 *
	 * @return the changes as map (key: changed property, value: new value)
	 */
	private Map<String, Object> findUiChanges() {
		Map<String, Object> changed = new HashMap<>();
		Map<String, Object> state   = getModel().getState();
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

	@Override
	public void onChange(String property, Object value) {
		updateView(property,value);
	}
}
