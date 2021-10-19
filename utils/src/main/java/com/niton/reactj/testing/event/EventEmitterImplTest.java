package com.niton.reactj.testing.event;

import com.niton.reactj.api.event.CustomEventEmitter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * If you implement an {@link CustomEventEmitter} use this test template to test your implementation.
 * <p>
 * The checks ensure that the behaviour is the same as described by the documentation
 *
 * @param <E> the event your implementation fires
 * @param <L> the type of the listener your emitter accepts
 */
public abstract class EventEmitterImplTest<E, L> {
	private CustomEventEmitter<E, L> eventManager;


	@BeforeEach
	void initManager() {
		eventManager = createManager();
	}

	protected abstract CustomEventEmitter<E, L> createManager();

	@Test
	void listen() {
		final List<Void> result = new ArrayList<>();
		eventManager.listen(createListener(() -> {
			result.add(null);
		}));
		eventManager.fire(getDummyEvent());
		assertEquals(1, result.size(), "Listener wasn't called");
	}

	/**
	 * Creates a listener that calles the `testCallback` when fired.
	 *
	 * @param testCallback the runnable to call when the event is triggered
	 * @return the listener that forwards to the callback
	 */
	protected abstract L createListener(Runnable testCallback);

	/**
	 * Create an event that can be used in `fire()`
	 *
	 * @return the event
	 */
	protected abstract E getDummyEvent();

	@Test
	void addListener() {
		eventManager.addListener(createListener(() -> {
		}));
		eventManager.addListener(createListener(() -> {
		}));
		eventManager.addListener(createListener(() -> {
		}));
		assertEquals(
				3, eventManager.getListeners().size(),
				"Adding 3 listeners to an empty event manager should result in 3 listeners in the manager list"
		);
	}

	@Test
	void addNull() {
		Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> eventManager.addListener(null),
				"Adding null listeners should throw IAE"
		);
	}

	@Test
	void removeListeners() {
		L listener = createListener(() -> {
		});
		eventManager.addListener(listener);
		eventManager.removeListeners();
		assertEquals(
				0,
				eventManager.getListeners().size(),
				"Removing a listener should remove it from the internal list"
		);
	}


	@Test
	void stopListener() {
		final List<Object> list = new ArrayList<>();
		L listener = createListener(() -> {
			list.add(null);
		});
		eventManager.addListener(listener);
		eventManager.removeListeners();
		eventManager.fire(getDummyEvent());
		assertEquals(0, list.size());
	}
}