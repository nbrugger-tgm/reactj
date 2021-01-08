package com.niton.reactj.test;

import com.niton.reactj.*;
import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.annotation.Unreactive;
import com.niton.reactj.exceptions.ReactiveException;
import org.junit.jupiter.api.Test;

import static com.niton.reactj.annotation.ReactivResolution.ReactiveResolutions.*;
import static org.junit.jupiter.api.Assertions.*;

public class AnnotationTest {
	private boolean aCalled = false,bCalled = false,cCalled = false,testCalled = false;
	public ReactiveProxy<DeepBase> deepProxy = ReactiveObject.create(DeepBase.class);
	public DeepBase                deep        = deepProxy.getObject();

	public ReactiveProxy<FlatBase> flatProxy = ReactiveObject.create(FlatBase.class);
	public FlatBase                flat        = flatProxy.getObject();

	public static final int SET1 = 12, SET2 = 22, SET3 = 83;

	private String stringDeposit;

	private int testDeposit;
	void testBiBinding() throws Throwable {
		ReactiveComponent deepComponent = new ReactiveComponent() {
			@Override
			public void createBindings(ReactiveBinder binder) {
				binder.bind("c", v -> cCalled = true);
				binder.bind("test",v->testCalled = true);

				binder.bindBi("c", v -> stringDeposit = v,()->stringDeposit,Integer::parseInt,String::valueOf);
				binder.bindBi("test",v->testDeposit = v,()->testDeposit);
			}
		};

		ReactiveController<ReactiveProxy<DeepBase>> controller = new ReactiveController<>(deepComponent);
		deep.setA(0);
		deep.setB(0);
		deep.setC(0);
		deepProxy.unbindAll();
		controller.bind(deepProxy);

		aCalled = false;
		bCalled = false;
		cCalled = false;
		testCalled = false;

		controller.update();
		assertEquals("0",stringDeposit);
		assertEquals(0,testDeposit);
		deep.setA(99);
		assertEquals(99,testDeposit);
		deep.setC(1234);
		assertEquals("1234",stringDeposit);

		stringDeposit = "987";
		controller.updateModel();
		assertEquals(987,deep.c);
	}

	<M extends Base> void test(ReactiveProxy<M> proxy,boolean a,boolean b,boolean test){
		ReactiveComponent deepComponent = new ReactiveComponent() {
			@Override
			public void createBindings(ReactiveBinder binder) {
				binder.bind("c",v -> cCalled = true);
				binder.bind("a", v -> aCalled = true);
				binder.bind("b",v -> bCalled = true);
				binder.bind("test",v->testCalled = true);
			}

			@Reactive("a")
			void aListener(){
				if(!a)
					fail("@Reactive rename should erase old name");
			}
			@Reactive("test")
			void aListener(Integer i){
				if(!test)
					fail("test is not allowed to be called");
				else
					if(i != 0)
						assertEquals(SET1,i);
			}
			@Reactive("test")
			void aListener(int i){
				if(!test)
					fail("test is not allowed to react");
				else
					if(i != 0)
						assertEquals(SET1,i);

			}
			@Reactive("b")
			void bListener(){
				if(!b)
					fail("@Unreactive disrespected");
			}

			@Reactive("c")
			void someMethod(int val){
				if(val != 0)
					assertEquals(SET3,val);
			}
		};
		ReactiveController<ReactiveProxy<M>> controller = new ReactiveController<>(deepComponent);
		controller.bind(proxy);

		aCalled = false;
		bCalled = false;
		cCalled = false;
		testCalled = false;
		proxy.getObject().setA(SET1);
		assertEquals(a,aCalled);
		assertEquals(test,testCalled);
		proxy.getObject().setB(SET2);
		assertEquals(b,bCalled);
	}



	@Test
	void testFlat(){
		test(flatProxy,false,false,false);
		flat.setC(SET3);
		assertTrue(cCalled);
	}

	@Test
	void testDeep(){
		test(deepProxy,false,false,true);
		deep.setC(SET3);
		assertTrue(cCalled);
	}

	@Test
	void errorTesting(){
		assertThrows(ReactiveException.class, () -> {
			ReactiveObject.create(FailBase.class);
		});

		assertThrows(ReactiveException.class, () -> {
			ReactiveObject.create(FlatBase.class,"Wrong type");
		});
		assertThrows(ReactiveException.class, () -> {
			ReactiveComponent deepComponent = new ReactiveComponent() {
				@Override
				public void createBindings(ReactiveBinder binder) {}


				@Reactive("a")
				void wrong(int too,int many){}
			};
			ReactiveController<ReactiveProxy<DeepBase>> controller = new ReactiveController<>(deepComponent);
		});
		assertThrows(ReactiveException.class, () -> {
			ReactiveComponent deepComponent = new ReactiveComponent() {
				@Override
				public void createBindings(ReactiveBinder binder) {}


				@Reactive("test")
				void wrong(String badArgument){}
			};
			ReactiveController<ReactiveProxy<DeepBase>> cont = new ReactiveController<>(deepComponent);
			cont.bind(deepProxy);
			deepProxy.unbind(cont);
		});

		assertThrows(ReactiveException.class, () -> {
			ReactiveComponent deepComponent = new ReactiveComponent() {
				@Override
				public void createBindings(ReactiveBinder binder) {}


				@Reactive("test")
				void errorProne(int value){
					throw new NullPointerException("Intentional error");
				}
			};
			ReactiveController<ReactiveProxy<DeepBase>> cont = new ReactiveController<>(deepComponent);
			cont.bind(deepProxy);
			deepProxy.unbind(cont);
		});

	}
	public static class Base {
		@Reactive("test")
		private int a;
		@Unreactive
		private int b;

		public void setA(int a) {
			this.a = a;
		}

		public void setB(int b) {
			this.b = b;
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
		public int c;

		public void setC(int c) {
			this.c = c;
		}
	}

	public static class FailBase extends Base {
		public int c;
		private FailBase(int wrong){};
	}
}
