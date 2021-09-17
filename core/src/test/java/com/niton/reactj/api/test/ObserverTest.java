package com.niton.reactj.api.test;

import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.react.ReactiveComponent;
import com.niton.reactj.api.react.ReactiveController;
import com.niton.reactj.api.annotation.ReactivResolution;
import com.niton.reactj.api.observer.ObjectObserver;
import com.niton.reactj.api.proxy.ProxyCreator;
import com.niton.reactj.api.proxy.ProxySubject;
import com.niton.reactj.api.react.ReactiveWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Collections;

import static com.niton.reactj.api.annotation.ReactivResolution.ReactiveResolutions.DEEP;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Observer.ObjectObserver")
public class ObserverTest {
	public String                  lastChanged;
	public Object                  lastValue;
	public String                  converted;
	public       int                       changeCounter = 0;
	public final ReactiveWrapper<TestData> personProxy   = ProxyCreator.create(new TestData());

	@Test
	@DisplayName("Live Object Reactive Proxy")
	public void testLiveProxyObserving() {
		TestData                td   = new TestData();
		TestData                      td2  = new TestData();
		ReactiveWrapper<TestData> rtd1 = ProxyCreator.create(td);
		ReactiveWrapper<TestData> rtd2 = ProxyCreator.create(td2);
		observerTest(rtd1, rtd2);
	}

	@Test
	@DisplayName("Live Object Subject Proxy")
	public void testLiveProxySubjectObserving() {
		SubjectTestData td   = new SubjectTestData();
		SubjectTestData td2  = new SubjectTestData();
		SubjectTestData rtd1 = ProxyCreator.create(td);
		SubjectTestData rtd2 = ProxyCreator.create(td2);
		observerTest(rtd1, rtd2);
	}

	@Test
	@DisplayName("Reactive Proxy")
	public void testProxyObserving() {
		observerTest(personProxy, ProxyCreator.create(new TestData()));
	}

	@Test
	@DisplayName("Reactive Subject Proxy")
	public void testReactiveSubjectObserving() {
		SubjectTestData d1 = ProxyCreator.create(new SubjectTestData());
		SubjectTestData d2 = ProxyCreator.create(new SubjectTestData());
		observerTest(d1, d2);
	}

	@Test
	@DisplayName("Reactive Subject method forwarding")
	public void testReactiveSubjectForwardDomain() throws Exception {
		SubjectTestData d1       = ProxyCreator.create(new SubjectTestData());
		d1.set("id", 12);
		assertEquals(12, d1.getId(), "Call to the reactive part of a Subject should be forwarded");
		d1.set("id", 15);
		assertEquals(15, d1.getId(), "Call to the reactive part of a Subject should be forwarded");
		d1.set(Collections.singletonMap("c", Color.CYAN));
		assertEquals(Color.CYAN,
		             d1.getC(),
		             "Call to the reactive part of a Subject should be forwarded");
	}


	@Test
	@DisplayName("Reactive Subject Proxy (no equals() imp.)")
	public void testNoEqualsReactiveSubjectObserving() {
		NonEqualSubjectTestData d1 = ProxyCreator.create(new NonEqualSubjectTestData());
		NonEqualSubjectTestData d2 = ProxyCreator.create(new NonEqualSubjectTestData());
		observerTest(d1, d2);
	}

	public <M extends Reactable> void observerTest(M obj, M newObj) {
		ObjectObserver<M> testDataObserver = new ObjectObserver<>();
		testDataObserver.addListener(change ->{
				System.out.println(change.propertyName + " changed to " + change.propertyValue);
				lastChanged = change.propertyName;
				lastValue   = change.propertyValue;
				changeCounter++;
		});

		testDataObserver.observe(obj);

		lastValue     = null;
		lastChanged   = null;
		changeCounter = 0;

		@SuppressWarnings("unchecked")
		TestData td = obj instanceof TestData ? (TestData) obj : (obj instanceof ReactiveWrapper ? ((ReactiveWrapper<? extends TestData>) obj)
			.getObject() : null);
		assert td != null;


		td.id = 0;
		assertNull(lastChanged, "Observer should not be triggered from assigment");
		assertNull(lastValue, "Observer should not be triggered from assigment");

		try {
			obj.set("id", 12);
			assertNull(lastChanged, "set(param,val) should not trigger observer");
		} catch(Exception throwable) {
			fail("ID should be set-able", throwable);
		}

		obj.react();
		assertEquals("id", lastChanged);
		assertEquals(12, lastValue);

		td.setC(Color.GREEN);
		assertNotNull(lastValue);
		assertEquals("c", lastChanged);
		assertEquals(Color.GREEN, lastValue);
		td.setId(99);
		assertEquals("id", lastChanged);
		td.setColor(Color.WHITE);
		assertEquals(Color.WHITE, lastValue);
		int oldCounter = changeCounter;
		testDataObserver.observe(obj);
		assertEquals(oldCounter,
		             changeCounter,
		             "Rebinding the same object should not create changes");
		testDataObserver.stopObservation();
		td.setId(9999);
		assertEquals(oldCounter, changeCounter, "Unbound is not working");

		testDataObserver.observe(newObj);
		assertEquals(testDataObserver.getObserved(), newObj);
	}

	@Test
	@DisplayName("Argument verification")
	void testArgumentVerification() {
		ObjectObserver<ReactiveWrapper<TestData>> observer = new ObjectObserver<>();
		assertThrows(IllegalArgumentException.class, () -> observer.observe(null));
	}

	@Test
	@DisplayName("binding")
	void bindingTest() {
		ReactiveWrapper<TestData> proxy = ProxyCreator.create(new TestData());
		TestData                      td    = proxy.getObject();

		ReactiveComponent<ReactiveWrapper<TestData>> testComponent = binder -> {
			binder.bind("id", val -> lastValue = val);
			binder.bind("c", val -> lastValue = val);
			binder.bind("c", val -> converted = val, (Color c) -> String.valueOf(c.getRed()));
		};
		ReactiveController<ReactiveWrapper<TestData>> controller = new ReactiveController<>(
			testComponent);
		controller.setModel(proxy);

		td.setColor(Color.GREEN);
		assertEquals(Color.GREEN, lastValue);
		assertEquals("0", converted);

		td.setId(123);
		assertEquals(123, lastValue);
		assertEquals("0", converted);
	}

	public static class TestData {
		protected int   id;
		protected Color c = Color.RED;

		public Color getC() {
			return c;
		}

		public void setC(Color c) {
			this.c = c;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setColor(Color c) {
			this.c = c;
		}
	}

	@ReactivResolution(DEEP)
	public static class SubjectTestData extends TestData implements ProxySubject {
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof TestData)) {
				return false;
			}
			return ((TestData) obj).c.equals(c) && ((TestData) obj).id == id;
		}
	}

	@ReactivResolution(DEEP)
	public static class NonEqualSubjectTestData extends TestData implements ProxySubject {

	}

}
