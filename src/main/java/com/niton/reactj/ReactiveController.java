package com.niton.reactj;

import com.niton.reactj.ReactiveBinder.BiBinding;
import com.niton.reactj.ReactiveBinder.Binding;
import com.niton.reactj.util.ReactiveComponentUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.niton.reactj.exceptions.ReactiveException.*;

/**
 * A reactive controller is responsible to communicate changes between ReactableObjects and ReactiveComponents.<br>
 * Use this to connect a View and a Model
 *
 * @param <M> Model Type (might be a {@link ReactiveProxy})
 */
public final class ReactiveController<M extends Reactable> extends Observer<M> {

	private final Map<String, List<Binding<?, ?>>>   displayBindings = new ConcurrentHashMap<>();
	private final Map<String, List<BiBinding<?, ?>>> editBindings    = new ConcurrentHashMap<>();
	private       boolean                            blockReactions;

	/**
	 * @param view the view or component to control. Most likely a UI element
	 */
	public ReactiveController(ReactiveComponent view) {
		//Maybe in the future it is needed to add the view as field
		ReactiveBinder binder = new ReactiveBinder(this::updateModel,
		                                           displayBindings,
		                                           editBindings);
		view.createBindings(binder);
		ReactiveComponentUtil.createAnnotatedBindings(view, binder);
	}

	/**
	 * Pulls all changes from the ReactiveComponent (connected by {@link ReactiveBinder#bindBi(String, ReactiveBinder.DisplayFunction, ReactiveBinder.ValueReceiver)}) and applies them to the model.
	 * If everything is done right this method is called automatically, and there is no need to call this by yourself
	 * @throws Throwable if there is some reflection problems
	 */
	public void updateModel() throws Throwable {
		updateModel(null);
	}

	/**
	 * Pulls changes from the Component to the Model
	 * @param unused not used
	 */
	private void updateModel(Object unused) {
		if (blockReactions) {
			return;
		}
		Map<String, Object> changed = findUiChanges();

		if (!changed.isEmpty()) {
			M model = getModel();
			model.set(changed);
			model.react();
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
	 * Sends a signal to all bindings with the regarding key
	 *
	 * @param key   the key to react to (most likely the name of the changed property)
	 * @param value the value the event carries
	 */
	private void updateView(final String key, final Object value) {
		List<Binding<?, ?>> bindings = displayBindings.get(key);
		if (bindings == null || bindings.isEmpty()) {
			return;
		}
		blockReactions = true;
		bindings.forEach(e -> updateBinding(key, value, e));
		blockReactions = false;
	}

	/**
	 * Triggers a binding
	 * @param key the name of the binding to trigger
	 * @param value the value to pass to the binding
	 * @param binding the binding to call
	 */
	private static void updateBinding(String key, Object value, Binding<?, ?> binding) {
		Object converted;
		try {
			converted = binding.getToDisplayConverter().convert(value);
		} catch (ClassCastException ex) {
			throw badConverterException(key, value.getClass());
		}

		if (binding instanceof BiBinding) {
			//ignore change when the value is already present
			Object present = ((BiBinding<?, ?>) binding).getReceiver().get();
			if (present.equals(value)) {
				return;
			}
		}
		try {
			binding.getDisplayFunction().display(converted);
		} catch (ClassCastException ex) {
			throw bindingException(key, value, converted, ex);
		}
	}


	@Override
	public void onChange(String property, Object value) {
		updateView(property, value);
	}
}
