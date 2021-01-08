package com.niton.reactj.test;

import com.niton.reactj.*;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class ObserverTest {
	public String                  lastChanged;
	public Object                  lastValue;
	public String                  converted;
	public int                     changeCounter = 0;
	public ReactiveProxy<TestData> personProxy   = ReactiveObject.create(TestData.class);
	public TestData                td            = personProxy.getObject();

	@Test
	public void testObserving(){
		Observer<ReactiveProxy<TestData>> testDataObserver = new Observer<ReactiveProxy<TestData>>() {
			@Override
			public void onChange(String property, Object value) {
				System.out.println(property+" changed to "+value);
				lastChanged = property;
				lastValue = value;
				changeCounter++;
			}
		};

		testDataObserver.bind(personProxy);

		lastValue = null;
		lastChanged = null;
		td.id = 0;
		assertNull(lastChanged,"Observer should not be triggered from assigment");
		assertNull(lastValue, "Observer should not be triggered from assigment");

		try {
			personProxy.set("id",12);
			assertNull(lastChanged,"set(param,val) should not trigger observer");
		} catch (Exception throwable) {
			fail("ID should be set-able");
		}

		personProxy.react();
		assertEquals("id",lastChanged);


		td.setC(Color.GREEN);
		assertNotNull(lastValue);
		assertEquals("c",lastChanged);
		assertEquals(Color.GREEN,lastValue);
		td.setId(99);
		assertEquals("id",lastChanged);
		td.setColor(Color.WHITE);
		assertEquals(Color.WHITE,lastValue);
		int oldCounter = changeCounter;
		testDataObserver.bind(personProxy);
		assertEquals(oldCounter, changeCounter,"Rebinding the same object should not create changes");
		personProxy.unbind(testDataObserver);
		td.setId(9999);
		assertEquals(oldCounter, changeCounter,"Unbound is not working");

		ReactiveProxy<TestData> newProxy = ReactiveObject.create(TestData.class);
		testDataObserver.bind(newProxy);
		assertEquals(testDataObserver.getModel(),newProxy);
	}

	@Test
	public void testArgumentVerification(){
		Observer<ReactiveProxy<TestData>> observer = new Observer<ReactiveProxy<TestData>>() {
			@Override
			public void onChange(String property, Object value) {
			}
		};
		assertThrows(IllegalArgumentException.class,()->observer.bind(null));
	}

	@Test
	public void bindingTest(){
		ReactiveProxy<TestData> proxy = ReactiveObject.create(TestData.class);
		TestData                td          = proxy.getObject();

		ReactiveComponent testComponent = new ReactiveComponent() {
			@Override
			public void createBindings(ReactiveBinder binder) {
				binder.bind("id", val -> lastValue = val);
				binder.bind("c",val -> lastValue = val);
				binder.bind("c",val -> converted = val,(Color c) -> String.valueOf(c.getRed()));
			}
		};
		ReactiveController<ReactiveProxy<TestData>> controller = new ReactiveController<>(testComponent);
		controller.bind(proxy);

		td.setColor(Color.GREEN);
		assertEquals(Color.GREEN,lastValue);
		assertEquals("0",converted);

		td.setId(123);
		assertEquals(123,lastValue);
		assertEquals("0",converted);
	}

	public static class TestData {
		public int   id;
		public Color c = Color.RED;

		public void setColor(Color c) {
			this.c = c;
		}

		public void setC(Color c) {
			this.c = c;
		}

		public void setId(int id) {
			this.id = id;
		}
	}

}
