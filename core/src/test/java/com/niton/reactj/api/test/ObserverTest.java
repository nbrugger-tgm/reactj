package com.niton.reactj.api.test;

import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.react.ReactiveComponent;
import com.niton.reactj.core.annotation.ReactiveResolution;
import com.niton.reactj.core.observer.ObjectObserver;
import com.niton.reactj.core.proxy.ProxySubject;
import com.niton.reactj.core.react.ReactiveController;
import com.niton.reactj.core.react.ReactiveProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Collections;
import java.util.Objects;

import static com.niton.reactj.core.annotation.ReactiveResolution.ReactiveResolutionType.DEEP;
import static com.niton.reactj.core.proxy.ProxyCreator.INSTANCE;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Observer.ObjectObserver")
class ObserverTest {
	public final ReactiveProxy<TestData> personProxy   = INSTANCE.create(new TestData());
	public       String                  lastChanged;
	public       Object                  lastValue;
	public       String                  converted;
	public       int                     changeCounter = 0;

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

	@ReactiveResolution(DEEP)
	public static class SubjectTestData extends TestData implements ProxySubject {
		@Override
		public int hashCode() {
			return Objects.hash(id, c);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof TestData)) return false;
			TestData testData = (TestData) o;
			return id == testData.id && Objects.equals(c, testData.c);
		}
	}

	@ReactiveResolution(DEEP)
	public static class NonEqualSubjectTestData extends TestData implements ProxySubject {

	}

	@Test
	@DisplayName("Live Object Reactive Proxy")
	void testLiveProxyObserving() {
		TestData td = new TestData();
		TestData td2 = new TestData();
		ReactiveProxy<TestData> rtd1 = INSTANCE.create(td);
		ReactiveProxy<TestData> rtd2 = INSTANCE.create(td2);
		observerTest(rtd1, rtd2);
	}

	public <M extends Reactable> void observerTest(M obj, M newObj) {
		ObjectObserver<M> testDataObserver = new ObjectObserver<>();
		testDataObserver.addListener(change -> {
			System.out.println(change.propertyName + " changed to " + change.propertyValue);
			lastChanged = change.propertyName;
			lastValue = change.propertyValue;
			changeCounter++;
		});

		testDataObserver.observe(obj);

		lastValue = null;
		lastChanged = null;
		changeCounter = 0;

		@SuppressWarnings("unchecked")
		TestData td = obj instanceof TestData ?
				(TestData) obj :
				(obj instanceof ReactiveProxy ? ((ReactiveProxy<? extends TestData>) obj)
						.getObject() : null);
		assert td != null;


		td.id = 0;
		assertNull(lastChanged, "Observer should not be triggered from assigment");
		assertNull(lastValue, "Observer should not be triggered from assigment");

		assertDoesNotThrow(() -> {
			obj.set("id", 12);
			assertNull(lastChanged, "set(param,val) should not trigger observer");
		}, "ID should be set-able");

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
		assertEquals(
				oldCounter,
				changeCounter,
				"Rebinding the same object should not create changes"
		);
		testDataObserver.stopObservation();
		td.setId(9999);
		assertEquals(oldCounter, changeCounter, "Unbound is not working");

		testDataObserver.observe(newObj);
		assertEquals(newObj, testDataObserver.getObserved());
	}

	@Test
	@DisplayName("Live Object Subject Proxy")
	void testLiveProxySubjectObserving() {
		SubjectTestData td = new SubjectTestData();
		SubjectTestData td2 = new SubjectTestData();
		SubjectTestData rtd1 = INSTANCE.create(td);
		SubjectTestData rtd2 = INSTANCE.create(td2);
		observerTest(rtd1, rtd2);
	}

	@Test
	@DisplayName("Reactive Proxy")
	void testProxyObserving() {
		observerTest(personProxy, INSTANCE.create(new TestData()));
	}

	@Test
	@DisplayName("Reactive Subject Proxy")
	void testReactiveSubjectObserving() {
		SubjectTestData d1 = INSTANCE.create(new SubjectTestData());
		SubjectTestData d2 = INSTANCE.create(new SubjectTestData());
		observerTest(d1, d2);
	}

	@Test
	@DisplayName("Reactive Subject method forwarding")
	void testReactiveSubjectForwardDomain() {
		SubjectTestData d1 = INSTANCE.create(new SubjectTestData());
		d1.set("id", 12);
		assertEquals(12, d1.getId(), "Call to the reactive part of a Subject should be forwarded");
		d1.set("id", 15);
		assertEquals(15, d1.getId(), "Call to the reactive part of a Subject should be forwarded");
		d1.set(Collections.singletonMap("c", Color.CYAN));
		assertEquals(
				Color.CYAN,
				d1.getC(),
				"Call to the reactive part of a Subject should be forwarded"
		);
	}

	@Test
	@DisplayName("Reactive Subject Proxy (no equals() imp.)")
	void testNoEqualsReactiveSubjectObserving() {
		NonEqualSubjectTestData d1 = INSTANCE.create(new NonEqualSubjectTestData());
		NonEqualSubjectTestData d2 = INSTANCE.create(new NonEqualSubjectTestData());
		observerTest(d1, d2);
	}

	@Test
	@DisplayName("Argument verification")
	void testArgumentVerification() {
		ObjectObserver<ReactiveProxy<TestData>> observer = new ObjectObserver<>();
		assertThrows(IllegalArgumentException.class, () -> observer.observe(null));
	}

	@Test
	@DisplayName("binding")
	void bindingTest() {
		ReactiveProxy<TestData> proxy = INSTANCE.create(new TestData());
		TestData td = proxy.getObject();

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

}
