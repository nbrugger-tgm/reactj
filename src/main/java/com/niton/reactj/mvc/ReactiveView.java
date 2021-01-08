package com.niton.reactj.mvc;

import com.niton.reactj.Reactable;
import com.niton.reactj.ReactiveComponent;
import com.niton.reactj.ReactiveController;

/**
 * A view with automatic binding
 *
 * @param <V> The base class of the view (eg. JPanel)
 * @param <M> The model class for this view
 */
public abstract class ReactiveView<V, M extends Reactable> implements ReactiveComponent {
	private final ReactiveController<M> controller;
	private final V                     view;

	public ReactiveView() {
		view            = createView();
		this.controller = new ReactiveController<>(this);
		registerListeners();
	}

	protected void registerListeners() {
	}

	/**
	 * Create the view and the layout.
	 * <b>Do not try to display anything from the model here. Just create the layout</b>
	 *
	 * @return the completed view
	 */
	protected abstract V createView();

	/**
	 * @return the model of the underlying controller
	 */
	public M getModel() {
		return controller.getModel();
	}


	/**
	 * Changes the data to display
	 *
	 * @param object the model to display
	 */
	public void setData(M object) {
		controller.bind(object);
	}

	public V getView() {
		return view;
	}

	public ReactiveController<M> getController() {
		return controller;
	}
}
