package com.niton.reactj;

/**
 * A view with automatic binding
 *
 * @param <C> The class to use for listeners
 * @param <V> The base class of the view (eg. JPanel)
 * @param <M> The model class for this view
 */
public abstract class ReactiveView<C, V, M extends Reactable> implements ReactiveComponent<C> {
	private final ReactiveController<C,M> controller;
	private final V                     view;

	public ReactiveView(C controller) {
		view            = createView();
		this.controller = new ReactiveController<>(this, controller);
	}

	/**
	 * Create the view and the layout
	 *
	 * @return the completed view
	 */
	protected abstract V createView();

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

	public ReactiveController<C,M> getController() {
		return controller;
	}
}
