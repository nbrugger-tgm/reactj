package com.niton.reactj.event.test;

import com.niton.reactj.event.CustomEventManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class EventManagerTest<E, L> {
	private CustomEventManager<E, L> eventManager;


	@BeforeEach
	void initManager() {
		eventManager = createManager();
	}

	protected abstract CustomEventManager<E, L> createManager();

	@Test
	void listen() {
		final List<Void> result = new ArrayList<>();
		eventManager.listen(createListener(() -> {
			result.add(null);
		}));
		eventManager.fire(getDummyEvent());
		assertEquals(1, result.size(), "Listener wasnt called");
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
		assertEquals(3, eventManager.getListeners().size(), "Adding 3 listeners to an empty event manager" +
				"should result in 3 listeners in the manager list");
	}

	@Test
	void addNull() {
		assertThrows(
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
		assertEquals(0, eventManager.getListeners().size(), "Removing a listener should remove it from the internal list");
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