package com.niton.reactj.test;

import com.niton.reactj.*;
import com.niton.reactj.annotation.ReactivResolution;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static com.niton.reactj.annotation.ReactivResolution.ReactiveResolutions.DEEP;
import static org.junit.jupiter.api.Assertions.*;

public class ObserverTest {
	public String                  lastChanged;
	public Object                  lastValue;
	public String                  converted;
	public int                     changeCounter = 0;
	public ReactiveProxy<TestData> personProxy   = ReactiveObject.createProxy(TestData.class);

	@Test
	public void testProxyObserving() {
		observerTest(personProxy,ReactiveObject.createProxy(TestData.class));
	}

	@Test
	public void testReactiveSubjectObserving(){
		SubjectTestData d1 = ReactiveObject.create(SubjectTestData.class);
		SubjectTestData d2 = ReactiveObject.create(SubjectTestData.class);
		observerTest(d1,d2);
	}


	@Test
	public void testNoEqualsReactiveSubjectObserving(){
		NonEqualSubjectTestData d1 = ReactiveObject.create(NonEqualSubjectTestData.class);
		NonEqualSubjectTestData d2 = ReactiveObject.create(NonEqualSubjectTestData.class);
		observerTest(d1,d2);
	}

	public <M extends Reactable>void observerTest(M obj,M newObj){
		Observer<M> testDataObserver = new Observer<M>() {
			@Override
			public void onChange(String property, Object value) {
				System.out.println(property+" changed to "+value);
				lastChanged = property;
				lastValue = value;
				changeCounter++;
			}
		};

		testDataObserver.bind(obj);

		lastValue = null;
		lastChanged = null;
		changeCounter = 0;

		TestData td = obj instanceof TestData ? (TestData) obj : (obj instanceof ReactiveProxy ? ((ReactiveProxy<? extends TestData>) obj).getObject() : null);
		assert td != null;
		td.id = 0;
		assertNull(lastChanged,"Observer should not be triggered from assigment");
		assertNull(lastValue, "Observer should not be triggered from assigment");

		try {
			obj.set("id",12);
			assertNull(lastChanged,"set(param,val) should not trigger observer");
		} catch (Exception throwable) {
			fail("ID should be set-able",throwable);
		}

		obj.react();
		assertEquals("id",lastChanged);
		assertEquals(12, lastValue);

		td.setC(Color.GREEN);
		assertNotNull(lastValue);
		assertEquals("c",lastChanged);
		assertEquals(Color.GREEN,lastValue);
		td.setId(99);
		assertEquals("id",lastChanged);
		td.setColor(Color.WHITE);
		assertEquals(Color.WHITE,lastValue);
		int oldCounter = changeCounter;
		testDataObserver.bind(obj);
		assertEquals(oldCounter, changeCounter,"Rebinding the same object should not create changes");
		obj.unbind(testDataObserver);
		td.setId(9999);
		assertEquals(oldCounter, changeCounter,"Unbound is not working");

		testDataObserver.bind(newObj);
		assertEquals(testDataObserver.getModel(),newObj);
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
		ReactiveProxy<TestData> proxy = ReactiveObject.createProxy(TestData.class);
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
	@ReactivResolution(DEEP)
	public static class SubjectTestData extends TestData implements ProxySubject{
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof TestData))
				return false;
			return ((TestData) obj).c.equals(c) && ((TestData) obj).id == id;
		}
	}
	@ReactivResolution(DEEP)
	public static class NonEqualSubjectTestData extends TestData implements ProxySubject{

	}

}
