package com.niton.reactj.core.mvc;

import com.niton.reactj.api.mvc.ReactiveComponent;
import com.niton.reactj.api.observer.Reactable;
import com.niton.reactj.core.observer.Reflective;
import com.niton.reactj.core.react.ReactiveBinder;
import com.niton.reactj.core.react.ReactiveController;

/**
 * A view with automatic binding
 *
 * @param <V> The base class of the view (e.g. JPanel)
 * @param <M> The model class for this view
 */
public abstract class ReactiveView<V, M extends Reactable & Reflective>
		implements ReactiveComponent<M> {
	private final ReactiveController<M> controller;
	private final V                     view;

	protected ReactiveView() {
		view       = createView();
		controller = new ReactiveController<>(this);
		registerListeners();
	}

	/**
	 * Create the view and the layout.
	 * <b>Do not try to display anything from the model here. Just create the layout</b><br>
	 * You should also store references to all components for binding in {@link #createBindings(ReactiveBinder)}
	 *
	 * @return the completed view
	 */
	protected abstract V createView();

	protected void registerListeners() {
		//empty by intend as it should only be overwritten if needed
	}

	/**
	 * @return the model displayed
	 */
	public M getData() {
		return controller.getModel();
	}


	/**
	 * Changes the data to display
	 *
	 * @param object the model to display
	 */
	public void setData(M object) {
		controller.setModel(object);
	}

	public V getView() {
		return view;
	}

	public ReactiveController<M> getController() {
		return controller;
	}
}
