package com.niton.reactj.testing.observer;

import com.niton.reactj.api.event.Listener;
import com.niton.reactj.api.observer.AbstractObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Implement this to test your Observer implementation against the spec
 *
 * @param <O> your observer implementation
 * @param <R> the result of your observer implementation
 * @param <S> the type to observable
 */
@TestInstance(Lifecycle.PER_CLASS)
public abstract class ObserverImplTest<O extends AbstractObserver<R, S>, R, S> {
	private       R           fired;
	private final Listener<R> listener = event -> fired = event;
	private       O           observer;

	protected R getFired() {
		return fired;
	}

	protected O getObserver() {
		return observer;
	}

	@BeforeEach
	void prepare() {
		fired    = null;
		observer = createObserverInstance();
		observer.addListener(listener);
	}

	protected abstract O createObserverInstance();

	@Test
	void fireOnChange() {
		observer.setObserveOnRebind(false);
		observer.observe(createObservableInstance());
		modify(observer.getObserved());
		assertNotNull(fired, "Modifying the observed object should call listeners on the observer");
	}

	protected abstract S createObservableInstance();

	/**
	 * Modifies the observable object in a way to trigger the observer
	 *
	 * @param observable the observable to modify
	 *
	 * @return the result expected to be produced by the observer
	 */
	protected abstract R modify(S observable);

	@Test
	void fireCorrectEvent() {
		observer.setObserveOnRebind(false);
		observer.observe(createObservableInstance());
		R res = modify(observer.getObserved());
		assertEquals(res, fired, "The result passed to the listeners wasn't the expected one");
	}

	@Test
	void stopObserving() {
		observer.setObserveOnRebind(false);
		observer.observe(createObservableInstance());
		observer.stopObservation();
		modify(observer.getObserved());
		assertNull(fired, "Observer.stopObservation() still reports changes");
	}

	@Test
	void reset() {
		S observable = createObservableInstance();
		modify(observable);
		observer.setObserveOnRebind(false);
		observer.observe(observable);
		observer.reset();
		assertNull(fired, "reset() is not allowed to trigger listeners when observeOnRebind is false");
		observer.setObserveOnRebind(true);
		observer.reset();
		assertNotNull(fired, "Observer is not reporting changes after reset");
	}

	@Test
	void dontObserveRebind() {
		observer.setObserveOnRebind(false);
		observer.observe(createObservableInstance());
		assertNull(fired, "when observeRebind is false `observe()` shouldn't call update()");
	}

	@Test
	void observeRebind() {
		observer.setObserveOnRebind(true);
		S observable = createObservableInstance();
		modify(observable);
		observer.observe(observable);
		assertNotNull(fired, "when observeRebind is true `observe()` should call update()");
	}

	@Test
	void observe() {
		assertNull(observer.getObserved(), "Observers shouldn't initially observe an object");
		observer.observe(createObservableInstance());
		assertNotNull(observer.getObserved(), "observe() should assign `observedObject`");
	}
}
