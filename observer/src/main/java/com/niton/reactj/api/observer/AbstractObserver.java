package com.niton.reactj.api.observer;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.event.Listener;
import com.niton.reactj.utils.event.GenericListener;

/**
 * The interface to use in order to create an observer.
 *
 * @param <T> type of the result of an observation
 * @param <O> the type to be observed (should implement {@link Reactable})
 */
public abstract class AbstractObserver<T, O> {
	private final GenericListener updateListener  = this::update;
	private final EventEmitter<T> observeEvent    = new EventEmitter<>();
	protected     O               observedObject;
	/**
	 * If this property is true, calling {@link #observe(Object)} will report all changes to listeners.
	 * Otherwise {@link #observe(Object)} will call {@link #reset()}
	 */
	private       boolean         observeOnRebind = true;

	public void addListener(Listener<T> listener) {
		observeEvent.addListener(listener);
	}

	public void removeListener(Listener<T> listener) {
		observeEvent.stopListening(listener);
	}

	/**
	 * Informs all listener that an observation was done
	 *
	 * @param observation the data of the observation
	 */
	protected final void fireObservation(T observation) {
		observeEvent.fire(observation);
	}

	/**
	 * The observer will now observe changes in the given object.
	 * <p>
	 * As one observer can just observe one object, the last object will not be observed anymore
	 *
	 * @param observable the object to observe
	 */
	public void observe(O observable) {
		if (observable == null)
			throw new IllegalArgumentException("Cannot observe null");

		if (observedObject != null)
			stopObservation();
		observedObject = observable;
		if (observable instanceof Reactable)
			((Reactable) observable).reactEvent().addListener(updateListener);
		if (observeOnRebind) {
			update();
		} else {
			reset();
		}
	}

	/**
	 * Stop observing <i>the current subject</i>
	 */
	public void stopObservation() {
		if (observedObject instanceof Reactable)
			((Reactable) observedObject).reactEvent().stopListening(updateListener);
	}

	/**
	 * This method is called when a change in the observer was observed and should be processed.
	 * <p>
	 * The exact change is not known yet, it is the job of this method to find the changes and report them using {@link
	 * #fireObservation(Object)}
	 */
	protected abstract void update();

	/**
	 * Resets the observer to a clean state. <br>
	 * This should trigger an {@link #update()} when {@link #observeOnRebind} is true
	 */
	public abstract void reset();

	/**
	 * @return the object that is currently under observation
	 */
	public O getObserved() {
		return observedObject;
	}

	public boolean isObservingRebind() {
		return observeOnRebind;
	}

	public void setObserveOnRebind(boolean observeOnRebind) {
		this.observeOnRebind = observeOnRebind;
	}
}
