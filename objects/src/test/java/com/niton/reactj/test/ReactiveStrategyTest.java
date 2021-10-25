package com.niton.reactj.test;

import com.niton.reactj.api.event.GenericListener;
import com.niton.reactj.objects.ReactiveStrategy;
import com.niton.reactj.objects.annotations.Reactive;
import com.niton.reactj.objects.annotations.Unreactive;
import com.niton.reactj.objects.proxy.ProxyCreator;
import com.niton.reactj.objects.proxy.ProxySubject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ReactiveStrategy")
class ReactiveStrategyTest {
	private final ProxyCreator    creator      = ProxyCreator.besideOrigin();
	private       boolean         triggerFlag;
	private final GenericListener flagListener = () -> triggerFlag = true;

	public static class UnreactiveObject implements ProxySubject {
		private int prop;

		@Unreactive
		public int getProp() {
			return prop;
		}

		@Unreactive
		public void setProp(int prop) {
			this.prop = prop;
		}
	}

	public static class ReactiveObject implements ProxySubject {
		private int prop;

		@Reactive
		public int getProp() {
			return prop;
		}

		@Reactive
		public void setProp(int prop) {
			this.prop = prop;
		}
	}

	public static class NeutralReactiveObject implements ProxySubject {
		private int prop;

		public int getProp() {
			return prop;
		}

		public void setProp(int prop) {
			this.prop = prop;
		}
	}

	@Test
	@DisplayName("NOTHING")
	void testNothing() {
		creator.setStrategy(ReactiveStrategy.NOTHING);
		ReactiveObject obj = creator.create(new ReactiveObject());
		triggerFlag = false;
		obj.reactEvent().addListener(() -> triggerFlag = true);
		obj.setProp(12);
		assertFalse(triggerFlag, "A proxy created with 'NOTHING' should not trigger 'react' event if the" +
		                         "@Reactive annotation is present");

	}

	@Test
	@DisplayName("SETTERS")
	void testSetters() {
		creator.setStrategy(ReactiveStrategy.SETTERS);
		var obj = creator.create(new ReactiveObject());
		triggerFlag = false;
		obj.reactEvent().addListener(flagListener);

		obj.getProp();
		assertFalse(triggerFlag, "A proxy created with 'SETTER' should not trigger 'react' event on 'not-setters', " +
		                         "even if the @Reactive annotation is present");

		obj.setProp(12);
		assertTrue(triggerFlag, "A proxy created with 'SETTER' should react to setters");

		triggerFlag = false;
		var obj2 = creator.create(new UnreactiveObject());
		obj2.reactEvent().addListener(flagListener);
		obj2.setProp(12);
		assertFalse(
				triggerFlag,
				"A proxy created with 'SETTER' should not trigger 'react' when @Unreactive is present, even if the method is a setter"
		);
	}

	@Test
	@DisplayName("ALL")
	void testAll() {
		creator.setStrategy(ReactiveStrategy.ALL);
		var obj = creator.create(new UnreactiveObject());
		triggerFlag = false;
		obj.reactEvent().addListener(flagListener);

		obj.setProp(12);
		assertFalse(triggerFlag, "A proxy created with 'NOTHING' should not trigger 'react' event if the" +
		                         "@Reactive annotation is present");

		var obj2 = creator.create(new NeutralReactiveObject());
		obj2.reactEvent().addListener(flagListener);

		obj2.getProp();
		assertTrue(triggerFlag, "A proxy created with 'ALL' should trigger 'react' on getters");

		triggerFlag = false;

		obj2.setProp(12);
		assertTrue(triggerFlag, "A proxy created with 'ALL' should trigger 'react' on setters");
	}

	@Test
	@DisplayName("ANNOTATED")
	void testAnnotated() {
		creator.setStrategy(ReactiveStrategy.ANNOTATED);
		var obj = creator.create(new ReactiveObject());
		triggerFlag = false;
		obj.reactEvent().addListener(() -> triggerFlag = true);
		obj.getProp();
		assertTrue(triggerFlag, "A proxy created with 'ANNOTATED' should trigger 'react' if the" +
		                        "@Reactive annotation is present");

	}
}