package com.niton.reactj.observers;

import com.niton.reactj.mvc.EventManager;
import com.niton.reactj.mvc.Listener;

/**
 * The interface to use in order to create an observer.
 * @param <T> type of the result of an observation
 * @param <O> the type to be observed
 */
public abstract class AbstractObserver<T,O> {
	private final EventManager<T> observeEvent = new EventManager<>();
	protected O subject;
	/**
	 * If this property is true, calling {@link #observe(Object)} will report all changes to listeners.
	 * Otherwise {@link #observe(Object)} will call {@link #reset()}
	 */
	private boolean observeOnRebind = true;

	public void addListener(Listener<T> listener) {
		observeEvent.addListener(listener);
	}

	public void removeListener(Listener<T> listener) {
		observeEvent.stopListening(listener);
	}

	/**
	 * Informs all listener that an observation was done
	 * @param observation the data of the observation
	 */
	protected final void fireObservation(T observation){
		observeEvent.fire(observation);
	}

	/**
	 * The observer will now observe changes in the given object.
	 *
	 * As one observer can just observe one object, the last object will not be observed anymore
	 *
	 *
	 * @param observable the object to observe
	 */
	public void observe(O observable){
		if(observable == null) {
			throw new IllegalArgumentException("Cannot observe null");
		}
		if(subject != null)
			stopObservation();
		subject = observable;
		if(observeOnRebind){
			update();
		}else{
			reset();
		}
	}

	/**
	 * Stop observing <i>the current subject</i>
	 */
	public abstract void stopObservation();

	/**
	 * This method is called when a change in the observer was observed and should be processed.
	 *
	 * The exact change is not known yet, it is the job of this method to find the changes and report them using {@link #fireObservation(Object)}
	 */
	protected abstract void update();

	/**
	 * Resets the observer to a clean state. This should not directly trigger an {@link #update()}
	 */
	public abstract void reset();

	/**
	 * @return the object that is currently under observation
	 */
	public O getObserved(){
		return subject;
	}
	public boolean isObservingRebind() {
		return observeOnRebind;
	}
	public void setObserveOnRebind(boolean observeOnRebind) {
		this.observeOnRebind = observeOnRebind;
	}
}
