package com.niton.reactj.api.mvc;

import com.niton.reactj.api.observer.Reactable;
import com.niton.reactj.core.react.ReactiveBinder;

/**
 * Used to create a component that reacts to a Reactable Model. Most likely used for Views and similar stuff.<br>
 * Annotate methods with @ReactiveListener for automatic bindings
 * (this only works in combination with {@link com.niton.reactj.core.react.ReactiveController})
 *
 * @param <M> the type of the model to react to in this component
 */
public interface ReactiveComponent<M extends Reactable> {
	/**
	 * Add bindings that define how to react to changes in the model.<br>
	 * Use {@code binder.<bindingMethod>} to bind a method to a change<br><br>
	 * <b>Example:</b><br>
	 * {@code
	 * binder.bind("name",nameLabel::setText);
	 * }<br>
	 * This would bind the name property to the name label -> name label always displays the name
	 *
	 * @param binder the binder to bind to (used to create the bindings)
	 */
	void createBindings(ReactiveBinder<M> binder);
}
