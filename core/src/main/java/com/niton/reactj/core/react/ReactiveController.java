package com.niton.reactj.core.react;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.mvc.ReactiveComponent;
import com.niton.reactj.api.observer.Reactable;
import com.niton.reactj.core.observer.ObjectObserver;
import com.niton.reactj.core.observer.PropertyObservation;
import com.niton.reactj.core.observer.Reflective;
import com.niton.reactj.core.react.ReactiveBinder.BiBinding;
import com.niton.reactj.core.react.ReactiveBinder.Binding;
import com.niton.reactj.core.react.ReactiveBinder.SuperBinding;
import com.niton.reactj.core.util.ReactiveComponentUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.niton.reactj.api.exceptions.ReactiveException.*;

/**
 * A reactive controller is responsible to communicate changes between ReactableObjects and ReactiveComponents.<br>
 * Use this to connect a View and a Model
 *
 * @param <M> Model Type
 */
public final class ReactiveController<M extends Reflective & Reactable> {
	private final ObjectObserver<M>                     observer                   = new ObjectObserver<>();
	private final Map<String, List<Binding<?, ?>>>      displayBindings            = new ConcurrentHashMap<>();
	private final Map<String, List<BiBinding<?, ?>>>    editBindings               = new ConcurrentHashMap<>();
	private final Map<String, List<SuperBinding<?, M>>> displaySuperBindings       = new ConcurrentHashMap<>();
	private final List<SuperBinding<?, M>>              globalDisplaySuperBindings = new LinkedList<>();
	private       boolean                               blockReactions;

	/**
	 * @param component the view or component to control. Most likely a UI element
	 */
	public ReactiveController(ReactiveComponent<M> component) {
		//Maybe in the future it is needed to add the view as field
		ReactiveBinder<M> binder = new ReactiveBinder<>(
				this::updateModel,
				displayBindings,
				displaySuperBindings,
				globalDisplaySuperBindings,
				editBindings
		);
		component.createBindings(binder);
		ReactiveComponentUtil.createAnnotatedBindings(component, binder);
		observer.addListener(this::updateView);
		observer.setObserveOnRebind(true);
	}

	/**
	 * Pulls changes from the Component to the Model
	 *
	 * @param unused not used, only present so one can use this method as method reference as listener
	 */
	private void updateModel(Object unused) {
		if (blockReactions)
			return;

		Map<String, Object> changed = findUiChanges();

		M subject = observer.getObserved();
		if (!changed.isEmpty()) {
			subject.set(changed);
			subject.react();
			observer.updateCache(changed);
		}
	}

	private void updateView(PropertyObservation change) {
		updateView(change.propertyName, change.propertyValue);
	}

	/**
	 * Searches for things that changed in the UI
	 *
	 * @return the changes as map (key: changed property, value: new value)
	 */
	private Map<String, Object> findUiChanges() {
		Map<String, Object> changed = new HashMap<>();
		Map<String, Object> state   = observer.getObserved().getState();
		for (Map.Entry<String, Object> field : state.entrySet()) {
			if (!editBindings.containsKey(field.getKey())) {
				continue;
			}
			findBindingChanges(changed, field.getKey(), field.getValue());
		}
		return changed;
	}

	/**
	 * Sends a signal to all bindings with the regarding key
	 *
	 * @param key   the key to react to (the name of the changed property)
	 * @param value the value the event carries (value of the changed property)
	 */
	private void updateView(final String key, final Object value) {
		List<Binding<?, ?>>      bindings      = displayBindings.getOrDefault(key, Collections.emptyList());
		List<SuperBinding<?, M>> superBindings = displaySuperBindings.getOrDefault(key, Collections.emptyList());

		blockReactions = true;
		superBindings.forEach(e -> e.display(observer.getObserved()));
		globalDisplaySuperBindings.forEach(e -> e.display(observer.getObserved()));
		bindings.forEach(binding -> updateBinding(key, value, binding));
		blockReactions = false;
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
		List<BiBinding<?, ?>> editBind = editBindings.get(field);
		for (BiBinding<?, ?> biBinding : editBind) {
			Object bindingVal = biBinding.getModelConverted();
			if (!Objects.equals(bindingVal, oldValue)) {
				changed.put(field, bindingVal);
				break;
			}
		}
	}

	/**
	 * Triggers a binding
	 *
	 * @param key     the name of the binding to trigger (used for exception messages)
	 * @param value   the value to pass to the binding
	 * @param binding the binding to call
	 */
	private static void updateBinding(String key, Object value, Binding<?, ?> binding) {
		Object converted = convertToDisplay(key, value, binding);
		if (binding instanceof BiBinding) {
			BiBinding<?, ?> biBinding    = (BiBinding<?, ?>) binding;
			var             displayValue = biBinding.getDisplayValue();
			//ignore change when the value is already present
			if (Objects.equals(displayValue, converted)) {
				return;
			}
		}
		try {
			binding.display(converted);
		} catch (ClassCastException ex) {
			throw bindingException(key, value, converted, ex);
		}
	}

	/**
	 * Converts the value assoiated with key with the converter of the binding (to display type)
	 *
	 * @param key     the property name the binder is bound to; used for error messages
	 * @param value   the value that was recived and needs to be converted
	 * @param binding the binding to use as converter
	 *
	 * @return the converted value
	 *
	 * @throws ReactiveException when conversion fails
	 */
	private static Object convertToDisplay(String key, Object value, Binding<?, ?> binding) {
		try {
			return binding.convertToDisplay(value);
		} catch (ClassCastException ex) {
			throw badConverterException(key, value.getClass());
		}
	}

	/**
	 * Pulls all changes from the ReactiveComponent (connected by {@link ReactiveBinder#bindBi(String,
	 * ReactiveBinder.DisplayFunction, ReactiveBinder.ValueReceiver)}) and applies them to the model.
	 * If everything is done right this method is called automatically, and there is no need to call this by yourself
	 */
	public void updateModel() {
		updateModel(null);
	}

	public M getModel() {
		return observer.getObserved();
	}

	public void setModel(M model) {
		observer.observe(model);
	}

	public void update() {
		observer.update();
	}

	/**
	 * Clears all values and removes the model -> stops listening
	 */
	public void stop() {
		observer.stopObservation();
		observer.reset();
	}
}
