package com.niton.reactj.observer.test;

import com.niton.reactj.observer.AbstractObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractObserverTest {
	AbstractObserver<ObserverResult, Observable> observer;
	private boolean observationStopped;
	private boolean observationFired;

	@BeforeEach
	void resetState() {
		observationStopped = false;
		observationFired = false;
		observer = new AbstractObserver<>() {
			@Override
			public void stopObservation() {
				observationStopped = true;
			}

			@Override
			protected void update() {
				fireObservation(new ObserverResult());
			}

			@Override
			public void reset() {
				//not needed in this test
			}
		};
		observer.addListener(event -> observationFired = true);
	}

	@Test
	void observeRebind() {
		observer.setObserveOnRebind(true);
		observer.observe(new Observable());
		assertTrue(observationFired, "when observeRebind is true `observe()` should call update()");
	}

	@Test
	void dontObserveRebind() {
		observer.setObserveOnRebind(false);
		observer.observe(new Observable());
		assertFalse(observationFired, "when observeRebind is false `observe()` shouldn't call update()");
	}

	@Test
	void rebinding() {
		observer.observe(new Observable());
		assertFalse(observationStopped, "First observe call shouldn't trigger stopObservation()");
		observer.observe(new Observable());
		assertTrue(observationStopped, "Observing a new observable should stop observation of previous observable");
	}

	@Test
	void observe() {
		assertNull(observer.getObserved(), "Observers shouldn't initially observe an object");
		observer.observe(new Observable());
		assertNotNull(observer.getObserved(), "observe() should assign `observedObject`");
	}

	static class Observable {
	}

	static class ObserverResult {
	}

}