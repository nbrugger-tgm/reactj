package com.niton.reactj.testing.observer;

import com.niton.reactj.api.observer.AbstractObserver;

/**
 * This observer shall only be used for testing.
 * <p>
 * Fires an empty Observation on every {@link #update()} call
 */
public class MockObserver extends AbstractObserver<MockObserver.Observation, Object> {
	public static class Observation {
	}

	@Override
	protected void update() {
		fireObservation(new Observation());
	}

	@Override
	public void reset() {
		if (isObservingRebind())
			fireObservation(new Observation());
	}
}
