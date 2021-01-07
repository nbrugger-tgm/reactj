package com.niton.reactj.test;

import com.niton.reactj.Observer;
import com.niton.reactj.ReactiveComponent;
import com.niton.reactj.ReactiveProxy;
import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.annotation.Unreactive;
import com.niton.reactj.exceptions.ReactiveException;
import com.niton.reactj.mvc.ReactiveBinder;
import com.niton.reactj.mvc.ReactiveController;
import com.niton.reactj.mvc.ReactiveObject;
import org.junit.jupiter.api.Test;

import static com.niton.reactj.annotation.ReactivResolution.ReactiveResolutions.*;
import static org.junit.jupiter.api.Assertions.*;

public class AnnotationTest {
	private boolean aCalled = false,bCalled = false,cCalled = false,testCalled = false;
	public ReactiveProxy<DeepBase> deepProxy = ReactiveObject.create(DeepBase.class);
	public DeepBase                deep        = deepProxy.object;

	public ReactiveProxy<FlatBase> flatProxy = ReactiveObject.create(FlatBase.class);
	public FlatBase                flat        = flatProxy.object;

	public static final int SET1 = 12, SET2 = 22, SET3 = 83;

	<M extends Base> void test(ReactiveProxy<M> proxy,boolean a,boolean b,boolean test){
		ReactiveComponent<AnnotationTest> deepComponent = new ReactiveComponent<AnnotationTest>() {
			@Override
			public void createBindings(ReactiveBinder binder) {
				binder.bind("c",v -> cCalled = true);
				binder.bind("a", v -> aCalled = true);
				binder.bind("b",v -> bCalled = true);
				binder.bind("test",v->testCalled = true);
			}

			@Override
			public void registerListeners(AnnotationTest controller) {}

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
		ReactiveController<AnnotationTest,ReactiveProxy<M>> controller = new ReactiveController<>(deepComponent,this);
		controller.bind(proxy);

		aCalled = false;
		bCalled = false;
		cCalled = false;
		testCalled = false;
		proxy.object.setA(SET1);
		assertEquals(a,aCalled);
		assertEquals(test,testCalled);
		proxy.object.setB(SET2);
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
			ReactiveProxy<FlatBase> flatProxy = ReactiveObject.create(FlatBase.class);
			FlatBase                flat        = flatProxy.object;
		});

		assertThrows(ReactiveException.class, () -> {
			ReactiveProxy<FlatBase> flatProxy = ReactiveObject.create(FlatBase.class,"Wrong type");
			FlatBase                flat        = flatProxy.object;
		});
		assertThrows(ReactiveException.class, () -> {
			ReactiveComponent<AnnotationTest> deepComponent = new ReactiveComponent<AnnotationTest>() {
				@Override
				public void createBindings(ReactiveBinder binder) {}

				@Override
				public void registerListeners(AnnotationTest controller) {}

				@Reactive("a")
				void wrong(int too,int many){}
			};
		});
		assertThrows(ReactiveException.class, () -> {
			ReactiveComponent<AnnotationTest> deepComponent = new ReactiveComponent<AnnotationTest>() {
				@Override
				public void createBindings(ReactiveBinder binder) {}

				@Override
				public void registerListeners(AnnotationTest controller) {}

				@Reactive("test")
				void wrong(String badArgument){}
			};
			ReactiveController<AnnotationTest,ReactiveProxy<DeepBase>> cont = new ReactiveController<>(deepComponent,this);
			cont.bind(deepProxy);
		});

		assertThrows(ReactiveException.class, () -> {
			ReactiveComponent<AnnotationTest> deepComponent = new ReactiveComponent<AnnotationTest>() {
				@Override
				public void createBindings(ReactiveBinder binder) {}

				@Override
				public void registerListeners(AnnotationTest controller) {}

				@Reactive("test")
				void errorProne(int value){
					throw new NullPointerException("Intentional error");
				}
			};
			ReactiveController<AnnotationTest,ReactiveProxy<DeepBase>> cont = new ReactiveController<>(deepComponent,this);
			cont.bind(deepProxy);
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
		private FailBase(int wröng){};
	}
}
