package com.niton.reactj.test;

import com.niton.reactj.Reactable;
import com.niton.reactj.ReactiveComponent;
import com.niton.reactj.ReactiveController;
import com.niton.reactj.ReactiveProxy;
import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.observers.ObjectObserver;
import com.niton.reactj.proxy.ProxyCreator;
import com.niton.reactj.proxy.ProxySubject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Collections;

import static com.niton.reactj.annotation.ReactivResolution.ReactiveResolutions.DEEP;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Observer.ObjectObserver")
public class ObserverTest {
	public String                  lastChanged;
	public Object                  lastValue;
	public String                  converted;
	public       int                     changeCounter = 0;
	public final ReactiveProxy<TestData> personProxy   = ProxyCreator.wrapper(TestData.class);

	@Test
	@DisplayName("Live Object Reactive Proxy")
	public void testLiveProxyObserving() {
		TestData                td   = new TestData();
		TestData                      td2  = new TestData();
		ReactiveProxy<TestData> rtd1 = ProxyCreator.wrap(td);
		ReactiveProxy<TestData> rtd2 = ProxyCreator.wrap(td2);
		observerTest(rtd1, rtd2);
	}

	@Test
	@DisplayName("Live Object Subject Proxy")
	public void testLiveProxySubjectObserving() {
		SubjectTestData td   = new SubjectTestData();
		SubjectTestData td2  = new SubjectTestData();
		SubjectTestData rtd1 = ProxyCreator.wrapSubject(td);
		SubjectTestData rtd2 = ProxyCreator.wrapSubject(td2);
		observerTest(rtd1, rtd2);
	}

	@Test
	@DisplayName("Reactive Proxy")
	public void testProxyObserving() {
		observerTest(personProxy, ProxyCreator.wrapper(TestData.class));
	}

	@Test
	@DisplayName("Reactive Subject Proxy")
	public void testReactiveSubjectObserving() {
		SubjectTestData d1 = ProxyCreator.subject(SubjectTestData.class);
		SubjectTestData d2 = ProxyCreator.subject(SubjectTestData.class);
		observerTest(d1, d2);
	}

	@Test
	@DisplayName("Reactive Subject method forwarding")
	public void testReactiveSubjectForwardDomain() throws Exception {
		SubjectTestData d1       = ProxyCreator.subject(SubjectTestData.class);
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
	@DisplayName("Reactive Subject Proxy (no equals imp.)")
	public void testNoEqualsReactiveSubjectObserving() {
		NonEqualSubjectTestData d1 = ProxyCreator.subject(NonEqualSubjectTestData.class);
		NonEqualSubjectTestData d2 = ProxyCreator.subject(NonEqualSubjectTestData.class);
		observerTest(d1, d2);
	}

	public <M extends Reactable> void observerTest(M obj, M newObj) {
		ObjectObserver<M> testDataObserver = new ObjectObserver<M>();
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
		TestData td = obj instanceof TestData ? (TestData) obj : (obj instanceof ReactiveProxy ? ((ReactiveProxy<? extends TestData>) obj)
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
	public void testArgumentVerification() {
		ObjectObserver<ReactiveProxy<TestData>> observer = new ObjectObserver<>();
		assertThrows(IllegalArgumentException.class, () -> observer.observe(null));
	}

	@Test
	@DisplayName("binding")
	public void bindingTest() {
		ReactiveProxy<TestData> proxy = ProxyCreator.wrapper(TestData.class);
		TestData                      td    = proxy.getObject();

		ReactiveComponent<ReactiveProxy<TestData>> testComponent = binder -> {
			binder.bind("id", val -> lastValue = val);
			binder.bind("c", val -> lastValue = val);
			binder.bind("c", val -> converted = val, (Color c) -> String.valueOf(c.getRed()));
		};
		ReactiveController<ReactiveProxy<TestData>> controller = new ReactiveController<>(
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
