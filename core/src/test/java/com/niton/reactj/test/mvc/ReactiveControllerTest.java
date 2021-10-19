package com.niton.reactj.test.mvc;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.mvc.ReactiveComponent;
import com.niton.reactj.core.annotation.ReactiveListener;
import com.niton.reactj.core.proxy.ProxyCreator;
import com.niton.reactj.core.proxy.ReactiveProxy;
import com.niton.reactj.core.react.ReactiveBinder;
import com.niton.reactj.core.react.ReactiveController;
import com.niton.reactj.test.models.Base;
import com.niton.reactj.test.models.DeepBase;
import com.niton.reactj.test.models.FlatBase;
import com.niton.reactj.test.models.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReactiveController")
class ReactiveControllerTest {
	public static final int
			SET_A = 12,
			SET2  = 22,
			SET_C = 83;
	private static ProxyCreator            creator;
	public final   ReactiveProxy<DeepBase> deepProxy = creator.create(new DeepBase());
	public final   DeepBase                deep      = deepProxy.getObject();
	public final   ReactiveProxy<FlatBase> flatProxy = creator.create(new FlatBase());
	public final   FlatBase                flat      = flatProxy.getObject();
	private        boolean
	                                       aCalled   = false,
			bCalled                                  = false,
			cCalled                                  = false;
	private String stringDeposit;
	private int    intDeposit;
	private Object lastValue;
	private Object converted;

	static class TestObject {
		private int someInt;
	}

	@BeforeAll
	static void createCreator() {
		creator = ProxyCreator.besideOrigin();
		//only for testing purposes- plz don't do this <3
		creator.setAllowUnsafeProxies(true);
	}

	@Test
	@DisplayName("@ReactiveListener too many arguments")
	void tooManyReactiveListenerArgs() {
		ReactiveComponent<ReactiveProxy<Object>> deepComponent = new ReactiveComponent<ReactiveProxy<Object>>() {
			@Override
			public void createBindings(ReactiveBinder<ReactiveProxy<Object>> binder) {
				//unused for test
			}

			@ReactiveListener("someInt")
			void wrong(int too, int many) {
			}
		};
		assertThrows(ReactiveException.class, () -> {
			new ReactiveController<>(deepComponent);
		}, "@ReactiveListeners are not allowed to have more than one parameter, and if they do, an exception" +
				             "should be thrown");
	}

	@Test
	@DisplayName("@ReactiveListener bad argument")
	void errorTesting() {
		ReactiveComponent<ReactiveProxy<TestObject>> badTypedComponent
				= new ReactiveComponent<ReactiveProxy<TestObject>>() {
			@Override
			public void createBindings(ReactiveBinder<ReactiveProxy<TestObject>> binder) {
			}


			@ReactiveListener("someInt")
			void wrong(String badArgument) {
			}
		};
		ReactiveController<ReactiveProxy<TestObject>> badTypedController = new ReactiveController<>(
				badTypedComponent
		);
		var proxy = creator.create(new TestObject());
		assertThrows(ReactiveException.class, () -> {
			badTypedController.setModel(proxy);
		}, "When a @ReactiveListener has a type that doesn't matches the property " +
				             "it should fail to set such a model");

	}

	@Test
	@DisplayName("@ReactiveListener internal error")
	void testInternalError() {
		ReactiveComponent<ReactiveProxy<TestObject>> internalErrorComponent =
				new ReactiveComponent<ReactiveProxy<TestObject>>() {
					@Override
					public void createBindings(ReactiveBinder<ReactiveProxy<TestObject>> binder) {

					}

					@ReactiveListener("someInt")
					void errorProne(int value) {
						throw new NullPointerException("Intentional error");
					}
				};

		ReactiveController<ReactiveProxy<TestObject>> internalErrorController =
				new ReactiveController<>(internalErrorComponent);
		var deepProxy = ProxyCreator.INSTANCE.create(new TestObject());
		assertThrows(ReactiveException.class, () -> {
			internalErrorController.setModel(deepProxy);
		}, "When a getter that is proxied throws an exception syncing should fail too");
	}

	@Test
	@DisplayName("bi-bindings")
	void testBiBinding() throws Throwable {
		ReactiveComponent<ReactiveProxy<DeepBase>> deepComponent = binder -> {
			binder.bind("c", v -> cCalled = true);

			binder.bindBi(
					"c",
					v -> stringDeposit = v,
					() -> stringDeposit,
					Integer::parseInt,
					String::valueOf
			);
			binder.bindBi("a", v -> {
				intDeposit = v;
				aCalled    = true;
			}, () -> intDeposit);
		};

		ReactiveController<ReactiveProxy<DeepBase>> controller = new ReactiveController<>(deepComponent);
		deep.setA(0);
		deep.setB(0);
		deep.setC(0);
		controller.stop();
		controller.setModel(deepProxy);

		aCalled = false;
		bCalled = false;
		cCalled = false;

		controller.update();
		assertEquals("0", stringDeposit, "Change not correctly detected");
		assertEquals(0, intDeposit, "Change not correctly detected");
		deep.setA(99);
		assertEquals(99, intDeposit);
		deep.setC(1234);
		assertEquals("1234", stringDeposit);

		stringDeposit = "987";
		controller.updateModel();
		assertEquals(987, deep.getC());
	}

	@Test
	@DisplayName("@ReactiveResolution FLAT")
	void testFlat() {
		test(flatProxy, false, false);
		flat.setC(SET_C);
		assertTrue(cCalled);
	}

	/**
	 * tests a proxy
	 *
	 * @param proxy the proxy to test
	 * @param a     if changes to field "a" should cause an update
	 * @param b     if changes to field "b" should cause an update
	 * @param <M>   the type of the proxy
	 */
	<M extends Base> void test(ReactiveProxy<M> proxy, boolean a, boolean b) {
		ReactiveComponent<ReactiveProxy<M>> deepComponent = new ReactiveComponent<ReactiveProxy<M>>() {
			@Override
			public void createBindings(ReactiveBinder<ReactiveProxy<M>> binder) {
				binder.bind("c", v -> cCalled = true);
				binder.bind("a", v -> aCalled = true);
				binder.bind("b", v -> bCalled = true);
			}

			@ReactiveListener("a")
			void aListener(Integer i) {
				if (!a) {
					fail("'a' is not allowed to be called");
				} else if (i != 0) {
					assertEquals(SET_A, i);
				}
			}

			@ReactiveListener("b")
			void bListener() {
				if (!b) {
					fail("@Unreactive disrespected");
				}
			}

			@ReactiveListener("c")
			void someMethod(int val) {
				if (val != 0) {
					assertEquals(SET_C, val);
				}
			}
		};
		ReactiveController<ReactiveProxy<M>> controller = new ReactiveController<>(deepComponent);
		controller.setModel(proxy);

		aCalled = false;
		bCalled = false;
		cCalled = false;
		proxy.getObject().setA(SET_A);
		assertEquals(a, aCalled);
		proxy.getObject().setB(SET2);
		assertEquals(b, bCalled);
	}

	@Test
	@DisplayName("@ReactiveResolution DEEP")
	void testDeep() {
		test(deepProxy, true, false);
		deep.setC(SET_C);
		assertTrue(cCalled);
	}

	@Test
	@DisplayName("simple binding")
	void bindingTest() {
		ReactiveProxy<TestData> proxy = creator.create(new TestData());
		TestData                td    = proxy.getObject();

		ReactiveComponent<ReactiveProxy<TestData>> testComponent = binder -> {
			binder.bind("id", val -> lastValue = val);
			binder.bind("c", val -> lastValue = val);
			binder.bind("c", val -> converted = val, (TestData.TestEnum c) -> c.name());
		};
		ReactiveController<ReactiveProxy<TestData>> controller = new ReactiveController<>(
				testComponent);
		controller.setModel(proxy);

		td.setColor(TestData.TestEnum.GREEN);
		assertEquals(TestData.TestEnum.GREEN, lastValue);
		assertEquals("GREEN", converted);

		td.setId(123);
		assertEquals(123, lastValue);
		assertEquals("GREEN", converted);
	}
}
