package com.niton.reactj.test.api.binding;

import com.niton.reactj.api.binding.builder.CallBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedReactiveBinder;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.utils.event.GenericEventEmitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("RunnableBindings")
class RunnableBindingTest {

	private final GenericEventEmitter   coolEvent  = new GenericEventEmitter();
	private final GenericEventEmitter   coolEvent2 = new GenericEventEmitter();
	private       int                   counter;
	//use the "exposed" to hide unneeded methods
	private       ExposedReactiveBinder builder;

	@BeforeEach
	void init() {
		coolEvent.removeListeners();
		coolEvent2.removeListeners();
		counter = 0;
		builder = new CallBuilder();
	}

	@Test
	@DisplayName(".call(runnable).on(event)")
	void testSimpleBinding() {
		builder
				.call(this::increment)
				.on(coolEvent);
		assertEquals(0, counter, "Creating a binding should not trigger the binding");
		coolEvent.fire();
		assertEquals(1, counter, "Firing the event passed to 'on()' should trigger the binding");
	}

	private void increment() {
		counter++;
	}

	@Test
	@DisplayName(".call(runnable).when(condition)")
	void testSimpleCondition() {
		builder.call(this::increment)
		       .when(Condition.YES)
		       .on(coolEvent);
		for (int i = 1; i <= 5; i++) {
			coolEvent.fire();
			assertEquals(i, counter, "If condition is YES, every event should trigger the binding");
		}
	}

	@Test
	@DisplayName(".call(runnable).when(YES).and(NO)")
	void testAndCondition() {
		builder.call(this::increment)
		       .when(Condition.YES)
		       .and(Condition.NO)
		       .on(coolEvent);
		for (int i = 0; i < 5; i++) {
			coolEvent.fire();
		}
		assertEquals(0, counter, "The condition when(YES).and(NO) should make the binding never fire");
	}

	@Test
	@DisplayName(".call(runnable).when(YES).or(NO)")
	void testOrCondition() {
		builder.call(this::increment)
		       .when(Condition.YES)
		       .or(Condition.NO)
		       .on(coolEvent);
		for (int i = 1; i <= 5; i++) {
			coolEvent.fire();
			assertEquals(i, counter, "The condition when(YES).or(NO) should make the binding always fire");
		}
	}

	@Test
	@DisplayName(".call(runnable).on(event1).andOn(event2)")
	void testMultipleEventRunnable() {
		builder.call(this::increment)
		       .on(coolEvent)
		       .andOn(coolEvent2);
		for (int i = 1; i <= 5; i++) {
			coolEvent.fire();
			assertEquals(i, counter, "When using on(ev1).andOn(ev2) ev1 should trigger the binding");
		}

		for (int i = 6; i <= 10; i++) {
			coolEvent2.fire();
			assertEquals(i, counter, "When using on(ev1).andOn(ev2) ev2 should trigger the binding");
		}
	}

	@Test
	@DisplayName(".call(runnable).and(runnable)")
	void testCallAndAnd() {
		builder.call(this::increment)
		       .and(this::increment)
		       .and(this::increment)
		       .on(coolEvent);
		for (int i = 1; i <= 5; i++) {
			coolEvent.fire();
			assertEquals(
					i * 3,
					counter,
					"When using call(r).and(r2).and(r3).on(ev) r,r2 and r3 should run on ev.fire()"
			);
		}
	}

	@Test
	@DisplayName(".call(runnable).also().call(runnable)")
	void testCallAlso() {
		builder.call(this::increment)
		       .andAlso()
		       .call(this::increment)
		       .on(coolEvent);
		for (int i = 1; i <= 5; i++) {
			coolEvent.fire();
			assertEquals(
					i * 2,
					counter,
					"When using call(r).andAlso().call(r2).on(ev) r and r2 should run on ev.fire()"
			);
		}
	}

	@Test
	@DisplayName(".call(runnable).also().call(runnable).and(runnable)")
	void testCallAndAlsoAnd() {
		builder.call(this::increment)
		       .andAlso()
		       .call(this::increment)
		       .and(this::increment)
		       .on(coolEvent);
		for (int i = 1; i <= 5; i++) {
			coolEvent.fire();
			assertEquals(
					i * 3,
					counter,
					"When using call(r).andAlso().call(r2).and(r3).on(ev) r, r2 and r3 should run on ev.fire()"
			);
		}
	}
}
