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
	private       boolean                                           blockReaction   = false;

	public ReactiveController(ReactiveComponent<C> view, C customController) {
		//Maybe in the future it is needed to ass the view as field
		ReactiveBinder binder = new ReactiveBinder(this::updateModel, displayBindings, editBindings);
		view.createBindings(binder);
		view.createAnnotatedBindings(binder);
		view.registerListeners(customController);
	}

	private void updateModel(EventObject actionEvent) throws Throwable {
		if (blockReaction) {
			return;
		}
		Map<String, Object> changed = new HashMap<>();
		Map<String, Object> state = model.getState();
		for (Map.Entry<String, Object> field : state.entrySet()) {
			if (!editBindings.containsKey(field.getKey()))
				continue;
			Object oldValue = field.getValue();
			List<ReactiveBinder.BiBinding<?, ?>> editBind = editBindings.get(field.getKey());

			for (ReactiveBinder.BiBinding<?, ?> biBinding : editBind) {
				Object bindingVal = biBinding.getToModelConverter().convert(biBinding.getReciver().get());
				if (!Objects.equals(bindingVal, oldValue)) {
					changed.put(field.getKey(), bindingVal);
					break;
				}
			}
		}
		for (Map.Entry<String, Object> change : changed.entrySet()) {
			model.set(change.getKey(), change.getValue());
		}
		if (changed.size() > 0)
			model.react();
		valueCache.putAll(changed);
	}

	public void bind(Reactable model) {
		model.bind(this);
		this.model = model;
		if (model instanceof ReactiveList)
			model.react(ListActions.INIT.id(), model);
		else
			modelChanged();
	}

	public void modelChanged() {
		Map<String, Object> changed = new HashMap<>();
		getChanges(changed);
		modelChanged(changed);
	}

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

	private void updateView(final String key, final Object value) {
		List<ReactiveBinder.Binding<?, ?>> bindings = displayBindings.get(key);
		if (bindings != null && bindings.size() > 0) {
			blockReaction = true;
			bindings.forEach(e -> {
				Object converted;
				try {
					converted = e.getToDisplayConverter().convert(value);
				} catch (ClassCastException ex) {
					Class<?> original = value.getClass();
					throw new ReactiveException("Bad converter. A converter for \"" + key + "\" doesnt accepts type " + original.getSimpleName());
				}

				if (e instanceof ReactiveBinder.BiBinding) {
					Object present = ((ReactiveBinder.BiBinding<?, ?>) e).getReciver().get();
					if (present.equals(value))
						return;
				}
				try {
					e.getDisplay().display(converted);
				} catch (ClassCastException ex) {
					Class<?> original = value.getClass();
					Class<?> convertedType = converted.getClass();
					ReactiveException exception;
					if (convertedType.equals(original))
						exception = new ReactiveException("Bad binding for \"" + key + "\". Target function doesnt accept type " + original.getTypeName());
					else
						exception = new ReactiveException("Bad binding for \"" + key + "\". Target function doesnt accepts converted (from " + original.getTypeName() + " to " + convertedType.getTypeName() + ")");
					exception.initCause(ex);
					throw exception;
				}
			});
			blockReaction = false;
		}
	}

	public Reactable getModel() {
		return model;
	}
}
