package com.niton.reactj.api.test.mvc;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.mvc.ReactiveComponent;
import com.niton.reactj.api.test.models.Base;
import com.niton.reactj.api.test.models.DeepBase;
import com.niton.reactj.api.test.models.FlatBase;
import com.niton.reactj.core.annotation.ReactiveListener;
import com.niton.reactj.core.proxy.ProxyCreator;
import com.niton.reactj.core.proxy.ReactiveProxy;
import com.niton.reactj.core.react.ReactiveBinder;
import com.niton.reactj.core.react.ReactiveController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.niton.reactj.core.proxy.ProxyCreator.INSTANCE;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReactiveController")
class ReactiveControllerTest {
	public static final int
			SET1 = 12,
			SET2 = 22,
			SET3 = 83;
	public final ReactiveProxy<DeepBase> deepProxy = INSTANCE.create(new DeepBase());
	public final DeepBase                deep      = deepProxy.getObject();
	public final ReactiveProxy<FlatBase> flatProxy = INSTANCE.create(new FlatBase());
	public final FlatBase                flat      = flatProxy.getObject();
	private      boolean
	                                     aCalled   = false,
			bCalled                                = false,
			cCalled                                = false;
	private String stringDeposit;
	private int    testDeposit;

	static class TestObject {
		private int someInt;
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
		var proxy = ProxyCreator.INSTANCE.create(new TestObject());
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
				testDeposit = v;
				aCalled     = true;
			}, () -> testDeposit);
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
		assertEquals(0, testDeposit, "Change not correctly detected");
		deep.setA(99);
		assertEquals(99, testDeposit);
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
		flat.setC(SET3);
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
					fail("a is not allowed to be called");
				} else if (i != 0) {
					assertEquals(SET1, i);
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
					assertEquals(SET3, val);
				}
			}
		};
		ReactiveController<ReactiveProxy<M>> controller = new ReactiveController<>(deepComponent);
		controller.setModel(proxy);

		aCalled = false;
		bCalled = false;
		cCalled = false;
		proxy.getObject().setA(SET1);
		assertEquals(a, aCalled);
		proxy.getObject().setB(SET2);
		assertEquals(b, bCalled);
	}

	@Test
	@DisplayName("@ReactiveResolution DEEP")
	void testDeep() {
		test(deepProxy, true, false);
		deep.setC(SET3);
		assertTrue(cCalled);
	}
}
