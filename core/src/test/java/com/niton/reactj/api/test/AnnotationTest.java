package com.niton.reactj.api.test;

import com.niton.reactj.api.annotation.ReactivResolution;
import com.niton.reactj.api.annotation.Reactive;
import com.niton.reactj.api.annotation.ReactiveListener;
import com.niton.reactj.api.annotation.Unreactive;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.proxy.ProxyCreator;
import com.niton.reactj.api.react.ReactiveBinder;
import com.niton.reactj.api.react.ReactiveComponent;
import com.niton.reactj.api.react.ReactiveController;
import com.niton.reactj.api.react.ReactiveProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.niton.reactj.api.annotation.ReactivResolution.ReactiveResolutions.DEEP;
import static com.niton.reactj.api.annotation.ReactivResolution.ReactiveResolutions.FLAT;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Annotations")
class AnnotationTest {
	public static final int
			SET1 = 12,
			SET2 = 22,
			SET3 = 83;
	public final ReactiveProxy<DeepBase> deepProxy = ProxyCreator.create(new DeepBase());
	public final DeepBase deep = deepProxy.getObject();

	public final ReactiveProxy<FlatBase> flatProxy = ProxyCreator.create(new FlatBase());
	public final FlatBase flat = flatProxy.getObject();

	private boolean
			aCalled = false,
			bCalled = false,
			cCalled = false,
			testCalled = false;

	private String stringDeposit;

	private int testDeposit;

	void testBiBinding() throws Throwable {
		ReactiveComponent<ReactiveProxy<DeepBase>> deepComponent = binder -> {
			binder.bind("c", v -> cCalled = true);
			binder.bind("test", v -> testCalled = true);

			binder.bindBi("c",
					v -> stringDeposit = v,
					() -> stringDeposit,
					Integer::parseInt,
					String::valueOf);
			binder.bindBi("test", v -> testDeposit = v, () -> testDeposit);
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
		testCalled = false;

		controller.update();
		assertEquals("0", stringDeposit, "Change not correctly detected");
		assertEquals(0, testDeposit, "Change not correctly detected");
		deep.setA(99);
		assertEquals(99, testDeposit);
		deep.setC(1234);
		assertEquals("1234", stringDeposit);

		stringDeposit = "987";
		controller.updateModel();
		assertEquals(987, deep.c);
	}

	<M extends Base> void test(ReactiveProxy<M> proxy, boolean a, boolean b, boolean test) {
		ReactiveComponent<ReactiveProxy<M>> deepComponent = new ReactiveComponent<ReactiveProxy<M>>() {
			@Override
			public void createBindings(ReactiveBinder<ReactiveProxy<M>> binder) {
				binder.bind("c", v -> cCalled = true);
				binder.bind("a", v -> aCalled = true);
				binder.bind("b", v -> bCalled = true);
				binder.bind("test", v -> testCalled = true);
			}

			@ReactiveListener("a")
			void aListener() {
				if (!a) {
					fail("@Reactive rename should erase old name");
				}
			}

			@ReactiveListener("test")
			void aListener(Integer i) {
				if (!test) {
					fail("test is not allowed to be called");
				} else if (i != 0) {
					assertEquals(SET1, i);
				}
			}

			@ReactiveListener("test")
			void aListener(int i) {
				if (!test) {
					fail("test is not allowed to react");
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
		testCalled = false;
		proxy.getObject().setA(SET1);
		assertEquals(a, aCalled);
		assertEquals(test, testCalled);
		proxy.getObject().setB(SET2);
		assertEquals(b, bCalled);
	}


	@Test
	@DisplayName("@ReactiveResolution FLAT")
	void testFlat() {
		test(flatProxy, false, false, false);
		flat.setC(SET3);
		assertTrue(cCalled);
	}

	@Test
	@DisplayName("@ReactiveResolution DEEP")
	void testDeep() {
		test(deepProxy, false, false, true);
		deep.setC(SET3);
		assertTrue(cCalled);
	}

	@Test
	@DisplayName("Test too many listener arguments")
	void tooManyReactiveListenerArgs() {
		ReactiveComponent<ReactiveProxy<DeepBase>> deepComponent = new ReactiveComponent<ReactiveProxy<DeepBase>>() {
			@Override
			public void createBindings(ReactiveBinder<ReactiveProxy<DeepBase>> binder) {
				//unused for test
			}

			@ReactiveListener("a")
			void wrong(int too, int many) {
			}
		};
		assertThrows(ReactiveException.class, () -> {
			new ReactiveController<>(deepComponent);
		}, "@ReactiveListeners are not allowed to have more than one parameter, and if they have an exception" +
				"should be thrown");
	}

	@Test
	@DisplayName("Exception throwing")
	void errorTesting() {
		ReactiveComponent<ReactiveProxy<DeepBase>> badTypedComponent = new ReactiveComponent<ReactiveProxy<DeepBase>>() {
			@Override
			public void createBindings(ReactiveBinder<ReactiveProxy<DeepBase>> binder) {
			}


			@ReactiveListener("test")
			void wrong(String badArgument) {
			}
		};
		ReactiveController<ReactiveProxy<DeepBase>> badTypedController = new ReactiveController<>(
				badTypedComponent
		);
		assertThrows(ReactiveException.class, () -> {
			badTypedController.setModel(deepProxy);
		}, "When a @ReactiveListener has a type that doesn't matches the property " +
				"it should fail to set such a model");

		ReactiveComponent<ReactiveProxy<DeepBase>> internalErrorComponent =
				new ReactiveComponent<ReactiveProxy<DeepBase>>() {
					@Override
					public void createBindings(ReactiveBinder<ReactiveProxy<DeepBase>> binder) {

					}

					@ReactiveListener("test")
					void errorProne(int value) {
						throw new NullPointerException("Intentional error");
					}
				};

		ReactiveController<ReactiveProxy<DeepBase>> internalErrorController =
				new ReactiveController<>(internalErrorComponent);
		assertThrows(ReactiveException.class, () -> {
			internalErrorController.setModel(deepProxy);
		}, "When a getter that is proxied throws an exception syncing should fail too");
	}

	public static class Base {
		@Reactive("test")
		private int a;
		@Unreactive
		private int b;

		public int getB() {
			return b;
		}

		public void setB(int b) {
			this.b = b;
		}

		public int getA() {
			return a;
		}

		public void setA(int a) {
			this.a = a;
		}
	}

	@ReactivResolution(DEEP)
	public static class DeepBase extends Base {
		private int c;

		public void setC(int c) {
			this.c = c;
		}
	}

	@ReactivResolution(FLAT)
	public static class FlatBase extends Base {
		private int c;

		public void setC(int c) {
			this.c = c;
		}
	}

	public static class FailBase extends Base {
		public int c;

		private FailBase(int wrong) {
			//intentional
		}

	}
}