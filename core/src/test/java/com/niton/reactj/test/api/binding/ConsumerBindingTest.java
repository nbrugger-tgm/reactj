package com.niton.reactj.test.api.binding;

import com.niton.reactj.api.binding.builder.CallBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedReactiveBinder;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.event.GenericEventEmitter;
import com.niton.reactj.objects.PropertyObservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConsumerBindings")
class ConsumerBindingTest {
	private static String                                    VAL1          = "SOME123";
	private final  int                                       VAL2          = 321;
	private final  GenericEventEmitter                       coolEvent     = new GenericEventEmitter();
	private final  EventEmitter<PropertyObservation<Object>> observerEvent = new EventEmitter<>();
	private final  EventEmitter<String>                      stringEvent   = new EventEmitter<>();
	private        String                                    source;
	private        String                                    received;

	//use the "exposed" to hide unneeded methods
	private ExposedReactiveBinder builder;


	@BeforeEach
	void init() {
		coolEvent.removeListeners();
		received = null;
		source   = null;
		observerEvent.removeListeners();
		builder = new CallBuilder();
	}

	@Test
	@DisplayName(".call(consumer).with(source)")
	void simpleConsumer() {
		builder.call(this::setReceived)
		       .with(this::getSomeValue)
		       .on(coolEvent);
		source = "HX300";
		coolEvent.fire();
		assertEquals(
				source,
				received,
				"call(a).with(b).on(ev) should call method a with the return value of b when event fires"
		);
	}

	private void setReceived(String str) {
		this.received = str;
	}

	private String getSomeValue() {
		return source;
	}

	@Test
	@DisplayName(".call(consumer).with(constant)")
	void constantConsumer() {
		String someConstant = "YE9984";
		builder.call(this::setReceived)
		       .withValue(someConstant)
		       .on(coolEvent);
		coolEvent.fire();
		assertEquals(
				someConstant,
				received,
				"call(a).with(constant).on(ev) should call method a constant as parameter"
		);
	}

	@Test
	@DisplayName(".call(consumer).on(event)")
	void callOnEvent() {
		builder.call(this::setReceived)
		       .on(stringEvent);
		String ev = "XYZ0098";
		stringEvent.fire(ev);
		assertEquals(ev, received, "call(a).on(ev) should call method a with the fired event");
	}

	@Test
	@DisplayName(".call(consumer).with(converter).from(event)")
	void convertCallOnEvent() {
		builder.call(this::setReceived)
		       .with((PropertyObservation<Object> e) -> e.propertyName)
		       .from(observerEvent);
		String propertyName = "surname";
		observerEvent.fire(new PropertyObservation<>(propertyName, "null", new Object()));
		assertEquals(
				propertyName,
				received,
				"call(a).with(converter).on(ev) should call method a with the converted fired event"
		);
	}

	@Test
	@DisplayName(".call(consumer).with(converter).from(source)")
	void convertedSource() {
		builder.call(this::setReceived)
		       .with(Integer::toHexString)
		       .from(this::getVAL2)
		       .on(coolEvent);
		coolEvent.fire();
		assertEquals(
				Integer.toHexString(VAL2),
				received,
				"call(a).with(converter).from(source) should call method a with the converted source return value"
		);
	}

	public int getVAL2() {
		return VAL2;
	}

	@Test
	@DisplayName(".call(consumer).with(source).when(YES)")
	void yesCondition() {
		builder.call(this::setReceived)
		       .with(this::getSomeValue)
		       .when(Condition.YES)
		       .on(coolEvent);
		source = "YW8876";
		coolEvent.fire();
		assertEquals(
				source,
				received,
				"call(a).with(source).when(YES) should trigger"
		);
	}

	@Test
	@DisplayName(".call(consumer).with(source).when(NO)")
	void noCondition() {
		builder.call(this::setReceived)
		       .with(this::getSomeValue)
		       .when(Condition.NO)
		       .on(coolEvent);
		source = "YW8876";
		coolEvent.fire();
		assertNull(received, "call(a).with(source).when(NO) should not trigger");
	}

	@Test
	@DisplayName(".call(consumer).with(source).when(YES).and(NO)")
	void yesAndNoCondition() {
		builder.call(this::setReceived)
		       .with(this::getSomeValue)
		       .when(Condition.YES)
		       .and(Condition.NO)
		       .on(coolEvent);
		source = "HG3658";
		coolEvent.fire();
		assertNull(received, "call(a).with(source).when(YES).and(NO) should not trigger");
	}

	@Test
	@DisplayName(".call(consumer).with(source).when(NO).or(YES)")
	void yesOrNoCondition() {
		builder.call(this::setReceived)
		       .with(this::getSomeValue)
		       .when(Condition.NO)
		       .or(Condition.YES)
		       .on(coolEvent);
		source = "YZ3514";
		coolEvent.fire();
		assertEquals(source, received, "call(a).with(source).when(NO).or(YES) should trigger");
	}

	@Test
	@DisplayName(".call(consumer).with(source).when(TRUE_PREDICATE)")
	void truePredicate() {
		builder.call(this::setReceived)
		       .with(this::getSomeValue)
		       .when(not(String::isBlank))
		       .on(coolEvent);
		source = "KJ7453";
		coolEvent.fire();
		assertEquals(source, received, "call(a).with(source).when(TRUE_PREDICATE) should trigger");
	}

	@Test
	@DisplayName(".call(consumer).with(source).when(FALSE_PREDICATE)")
	void falsePredicate() {
		builder.call(this::setReceived)
		       .with(this::getSomeValue)
		       .when(String::isBlank)
		       .on(coolEvent);
		source = "KJ7453";
		coolEvent.fire();
		assertNull(received, "call(a).with(source).when(FALSE_PREDICATE) should not trigger");
	}

	@Test
	@DisplayName(".call(consumer).with(source).when(FALSE_PREDICATE).and(TRUE_PREDICATE)")
	void falseAndTruePredicate() {
		builder.call(this::setReceived)
		       .with(this::getSomeValue)
		       .when(String::isBlank)
		       .and(not(String::isBlank))
		       .on(coolEvent);
		source = "GF7950";
		coolEvent.fire();
		assertNull(received, "call(a).with(source).when(FALSE_PREDICATE) should not trigger");
	}

	@Test
	@DisplayName(".call(consumer).with(source).when(TRUE_PREDICATE).or(FALSE_PREDICATE)")
	void trueOrFalsePredicate() {
		builder.call(this::setReceived)
		       .with(this::getSomeValue)
		       .when(not(String::isBlank))
		       .or(String::isBlank)
		       .on(coolEvent);
		source = "KJ7453";
		coolEvent.fire();
		assertEquals(
				source,
				received,
				"call(a).with(source).when(TRUE_PREDICATE).or(FALSE_PREDICATE) should trigger"
		);
	}

	@Test
	@DisplayName(".call(consumer).with(source).when(YES).and(TRUE_PREDICATE)")
	void truePredicateAndYes() {
		builder.call(this::setReceived)
		       .with(this::getSomeValue)
		       .when(Condition.YES)
		       .and(not(String::isBlank))
		       .on(coolEvent);
		source = "GF7950";
		coolEvent.fire();
		assertEquals(
				source,
				received,
				"call(a).with(source).when(YES).and(TRUE_PREDICATE) should trigger"
		);
	}

	@Test
	@DisplayName(".call(consumer).with(source).when(TRUE_PREDICATE).or(FALSE_PREDICATE)")
	void falsePredicateOrNo() {
		builder.call(this::setReceived)
		       .with(this::getSomeValue)
		       .when(String::isBlank)
		       .or(Condition.NO)
		       .on(coolEvent);
		source = "MG4307";
		coolEvent.fire();
		assertNull(
				received,
				"call(a).with(source).when(FALSE_PREDICATE).or(NO) should not trigger"
		);
	}

	@Test
	@DisplayName("call(runnable).andAlso().call(consumer).with(source)")
	void mixBindingsAndRunnables() {
		AtomicInteger counter = new AtomicInteger();
		builder
				.call(counter::incrementAndGet)
				.andAlso()
				.call(this::setReceived)
				.with(this::getSomeValue)
				.on(coolEvent);
		source = "LO6450";
		coolEvent.fire();
		String msg = "When runnables and consumers/bindings are mixed, all should be fired";
		assertEquals(1, counter.get(), msg);
		assertEquals(source, received, msg);
	}

	@Test
	@DisplayName("call(runnable).andAlso().call(consumer).with(source)")
	void mixBindingsAndRunnablesWithCondition() {
		AtomicInteger counter = new AtomicInteger();
		builder
				.call(counter::incrementAndGet)
				.andAlso()
				.call(this::setReceived)
				.with(this::getSomeValue)
				.when(Condition.NO)
				.on(coolEvent);
		source = "CF6491";
		coolEvent.fire();
		String msg = "When this is done: 'call(a).andAlso().call(b).when(condition)' the condition only applies to b";
		assertEquals(1, counter.get(), msg);
		assertNull(received, msg);
	}

	@Test
	@DisplayName("call(consumer1).and(consumer2).with(source)")
	void multipleConsumersSameSource() {
		builder
				.call(this::setReceived)
				.and(v -> VAL1 = v)
				.with(this::getSomeValue)
				.on(coolEvent);
		source = "KH9757";
		coolEvent.fire();
		assertEquals(received, source);
		assertEquals(
				VAL1,
				source,
				"When '.call(a).and(b).with(source)' is used, a and b should be called with the same source"
		);
	}
}
