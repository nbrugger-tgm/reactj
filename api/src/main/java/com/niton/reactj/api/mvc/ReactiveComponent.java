package com.niton.reactj.api.mvc;

import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.event.GenericEventEmitter;
import com.niton.reactj.api.observer.Observer;

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
    public final    GenericEventEmitter onUiUpdate = new GenericEventEmitter();
    /**
     * The observer that observes the model
     */
    protected final Observer<O, M>      observer;
    /**
     * The cached view (for re-use)
     */
    private         V                   view;

    /**
     * @param observer the correct observer implementation for the model type
     */
    protected ReactiveComponent(Observer<O, M> observer) {
        this.observer = observer;
    }

    /**
     * This method returns the view. Keep in mind that the view is cached and re-used.
     * Calling this for the first time will also register the bindings. So before the first call of
     * this method, won't work.
     *
     * @return the created or cached
     */
    public V getView() {
        if (view == null) {
            view = createView();
            initBindings();
        }
        return view;
    }

    /**
     * Instantiates a new view
     *
     * @return the view
     */
    protected abstract V createView();

    private void initBindings() {
        registerBindings(observer.onObservation);
        observer.addListener(e -> onUiUpdate.fire());
    }

    /**
     * Adds bindings that update the view when the model changes and vice versa<br>
     * The use of {@link BinderDsl} is recommended but not required.
     *
     * @param onObservation the event that will fire when the model changes
     */
    protected abstract void registerBindings(EventEmitter<O> onObservation);

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
