package com.niton.reactj.test.binding.runnable;

import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.runnable.ConditionalRunnable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ConditionalRunnableTest {
	@Test
	void invalidParams() {
		assertThrows(
				IllegalArgumentException.class,
				() -> new ConditionalRunnable(Condition.YES, null)
		);
		assertThrows(
				IllegalArgumentException.class,
				() -> new ConditionalRunnable(null, () -> {
				})
		);

		assertThrows(
				IllegalArgumentException.class,
				() -> new ConditionalRunnable(Condition.YES, () -> {
				}).setCondition(null)
		);

	}

	@Test
	void flow() {
		AtomicBoolean called = new AtomicBoolean(false);

		var runnable = new ConditionalRunnable(Condition.YES, () -> called.set(true));
		runnable.run();
		assertTrue(called.get());
		runnable.setCondition(Condition.NO);
		called.set(false);
		runnable.run();
		assertFalse(
				called.get(),
				"Runnable should not be called when condition is overwritten to NO"
		);
		runnable = new ConditionalRunnable(Condition.NO, () -> called.set(true));
		called.set(false);
		runnable.run();
		assertFalse(
				called.get(),
				"Runnable should not be called when condition is initially NO"
		);
	}
}