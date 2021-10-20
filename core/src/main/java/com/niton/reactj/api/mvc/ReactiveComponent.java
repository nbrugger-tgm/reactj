package com.niton.reactj.api.mvc;

import com.niton.reactj.api.binding.builder.ReactiveBinder;
import com.niton.reactj.api.observer.AbstractObserver;
import com.niton.reactj.core.mvc.ModelCallBuilder;
import com.niton.reactj.utils.event.EventEmitter;
import com.niton.reactj.utils.event.GenericEventEmitter;

/**
 * The base for reactive views
 *
 * @param <M> the type of the model (e.g. Person)
 * @param <O> the type of the observation (e.g. PropertyObservation)
 * @param <V> the type of the view (e.g. JPanel)
 */
public abstract class ReactiveComponent<M, O, V> {
	/**
	 * This event is fired every time the ui refreshes, caused by a change in the model
	 */
	public final    GenericEventEmitter    onUiUpdate = new GenericEventEmitter();
	protected final AbstractObserver<O, M> observer;
	private         V                      view;

	protected ReactiveComponent(AbstractObserver<O, M> observer) {this.observer = observer;}

	public V getView() {
		if (view == null) {
			view = createView();
			initBindings();
		}
		return view;
	}

	protected abstract V createView();

	private void initBindings() {
		registerBindings(new ReactiveBinder<>(this::createBuilder), observer.onObservation);
		observer.addListener(e -> onUiUpdate.fire());
	}

	protected abstract void registerBindings(
			ReactiveBinder<ModelCallBuilder<M>> builder,
			EventEmitter<O> observerEvent
	);

	private ModelCallBuilder<M> createBuilder() {
		return new ModelCallBuilder<>(this::getModel);
	}

	/**
	 * @return the data displayed on th component
	 */
	public M getModel() {
		return observer.getObserved();
	}

	/**
	 * Display a new model
	 */
	public void setModel(M model) {
		observer.observe(model);
	}

}
