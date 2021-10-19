package com.niton.reactj.test.proxy;

import com.niton.reactj.api.observer.Reactable;
import com.niton.reactj.core.annotation.ReactiveResolution;
import com.niton.reactj.core.observer.ObjectObserver;
import com.niton.reactj.core.observer.Reflective;
import com.niton.reactj.core.proxy.ProxyCreator;
import com.niton.reactj.core.proxy.ProxySubject;
import com.niton.reactj.core.proxy.ReactiveProxy;
import com.niton.reactj.test.models.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Objects;

import static com.niton.reactj.core.annotation.ReactiveResolution.ReactiveResolutionType.DEEP;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Object-Proxy")
class ProxyTest {
	private static final ProxyCreator creator = ProxyCreator.besideOrigin();

	static {
		creator.setAllowUnsafeProxies(true);
	}

	public String lastChanged;
	public Object lastValue;
	public int    changeCounter = 0;

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
	@DisplayName("ReactiveProxy<...>")
	void testLiveProxyObserving() {
		TestData                td   = new TestData();
		TestData                td2  = new TestData();
		ReactiveProxy<TestData> rtd1 = creator.create(td);
		ReactiveProxy<TestData> rtd2 = creator.create(td2);
		proxyTest(rtd1, rtd2);
	}

	private <M extends Reactable & Reflective> void proxyTest(M obj, M newObj) {
		ObjectObserver<M> testDataObserver = new ObjectObserver<>();
		testDataObserver.addListener(change -> {
			lastChanged = change.propertyName;
			lastValue   = change.propertyValue;
			changeCounter++;
		});

		testDataObserver.observe(obj);

		lastValue     = null;
		lastChanged   = null;
		changeCounter = 0;

		@SuppressWarnings("unchecked")
		TestData td = obj instanceof TestData ?
				(TestData) obj :
				(obj instanceof ReactiveProxy ? ((ReactiveProxy<? extends TestData>) obj)
						.getObject() : null);
		assert td != null;


		td.id = 0;
		assertNull(lastChanged, "Proxy should not be triggered from assigment");
		assertNull(lastValue, "Proxy should not be triggered from assigment");

		assertDoesNotThrow(() -> {
			obj.set("id", 12);
			assertNull(lastChanged, "set(param,val) should not trigger observer");
		}, "ID should be set-able");

		obj.react();
		assertEquals("id", lastChanged);
		assertEquals(12, lastValue);

		td.setC(TestData.TestEnum.GREEN);
		assertNotNull(lastValue);
		assertEquals("c", lastChanged);
		assertEquals(TestData.TestEnum.GREEN, lastValue);
		td.setId(99);
		assertEquals("id", lastChanged);
		td.setColor(TestData.TestEnum.BLUE);
		assertEquals(TestData.TestEnum.BLUE, lastValue);
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
	@DisplayName("Proxy Subject")
	void testLiveProxySubjectObserving() {
		SubjectTestData td   = new SubjectTestData();
		SubjectTestData td2  = new SubjectTestData();
		SubjectTestData rtd1 = creator.create(td);
		SubjectTestData rtd2 = creator.create(td2);
		proxyTest(rtd1, rtd2);
	}

	@Test
	@DisplayName("Reactable forwarding")
	void testForwardingToReactable() {
		SubjectTestData d1 = creator.create(new SubjectTestData());
		d1.set("id", 12);
		assertEquals(12, d1.getId(), "Call to the reactive part of a Subject should be forwarded");
		d1.set("id", 15);
		assertEquals(15, d1.getId(), "Call to the reactive part of a Subject should be forwarded");
		d1.set(Collections.singletonMap("c", TestData.TestEnum.BLUE));
		assertEquals(
				TestData.TestEnum.BLUE,
				d1.getC(),
				"Call to the reactive part of a Subject should be forwarded"
		);
	}

	@Test
	@DisplayName("Proxy Subject(no equals() imp.)")
	void testNoEqualsReactiveSubjectObserving() {
		NonEqualSubjectTestData d1 = creator.create(new NonEqualSubjectTestData());
		NonEqualSubjectTestData d2 = creator.create(new NonEqualSubjectTestData());
		proxyTest(d1, d2);
	}

}
