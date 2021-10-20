package com.niton.reactj.test.api.proxy;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.proxy.AbstractProxyCreator;
import com.niton.reactj.api.proxy.ProxyException;
import com.niton.reactj.api.react.ReactiveWrapper;
import com.niton.reactj.observer.infusion.BesideOriginInfuser;
import com.niton.reactj.observer.infusion.InfusionAccessProvider;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import static com.niton.reactj.test.api.proxy.SimpleProxyTest.*;
import static org.junit.jupiter.api.Assertions.*;

class AbstractProxyCreatorTest {

	private final static String fakeOrigin = "AJKSD7312";

	public static class ProxyCreatorTestImpl extends AbstractProxyCreator {
		protected ProxyCreatorTestImpl(InfusionAccessProvider accessor) {
			super(accessor);
		}

		public static Field getField(Class<?> proxyClass, String field) {
			return AbstractProxyCreator.getField(proxyClass, new HashMap<>(), field);
		}

		@Override
		//expose for testing
		public Object getOrigin(Object proxy) throws IllegalAccessException {
			return super.getOrigin(proxy);
		}

		@Override
		protected <T> Class<? extends T> createProxyClass(Class<? extends T> aClass) {
			assertNotProxy(aClass);
			return aClass;
		}

		@Override
		//expose for testing
		public <T> void setProxyFields(T object, Class<?> proxyClass, T proxy) {
			super.setProxyFields(object, proxyClass, proxy);
		}
	}

	public static class FakeProxy {
		public Object                  PROXY_ORIGIN = fakeOrigin;
		public ReactiveWrapper<Object> PROXY_WRAPPER;
	}

	public static class ValidBase {
		public final String yeet;
		private      String yeet2;

		public ValidBase(String yeet) {this.yeet = yeet;}
	}

	@Test
	void testErrorOnGetField() {
		assertDoesNotThrow(() -> ProxyCreatorTestImpl.getField(FakeProxy.class, "PROXY_WRAPPER"));
		assertThrows(
				ReactiveException.class,
				() -> ProxyCreatorTestImpl.getField(FakeProxy.class, "notAField")
		);
	}

	@Test
	void getOrigin() throws IllegalAccessException {
		var creator = new ProxyCreatorTestImpl(new BesideOriginInfuser(MethodHandles.lookup()));
		assertEquals(fakeOrigin, creator.getOrigin(new FakeProxy()));
	}

	@Test
	void allowsUnsafeProxies() {
		var creator = new ProxyCreatorTestImpl(new BesideOriginInfuser(MethodHandles.lookup()));
		assertFalse(creator.allowsUnsafeProxies(), "By default unsafe proxies should be forbidden");
		creator.setAllowUnsafeProxies(true);
		assertTrue(creator.allowsUnsafeProxies());
	}

	@Test
	void verifyOriginClass() {
		assertThrows(
				ReactiveException.class,
				() -> AbstractProxyCreator.verifyOriginClass(FakeProxy.class),
				"A class with a public mutable field should be forbidden"
		);
		assertDoesNotThrow(() -> AbstractProxyCreator.verifyOriginClass(ValidBase.class));
	}

	@Test
	void proxyFromProxy()
			throws NoSuchFieldException, InvocationTargetException, InstantiationException,
			       IllegalAccessException, NoSuchMethodException {
		var creator   = new ProxyCreatorTestImpl(new BesideOriginInfuser(MethodHandles.lookup()));
		var proxy     = buildSimpleProxy(MethodHandles.lookup());
		var proxClass = proxy.getClass();
		assertThrows(
				ProxyException.class,
				() -> creator.createProxyClass(proxClass),
				"Creating a proxy for a proxy should throw an exception"
		);
	}

	@Test
	void hasDefaultBuilder() {
		var creator = new ProxyCreatorTestImpl(new BesideOriginInfuser(MethodHandles.lookup()));
		assertNotNull(creator.getBuilder(), "A Proxy creator should contain a builder by default");
	}

	@Test
	void sync() throws NoSuchFieldException, InvocationTargetException, InstantiationException,
	                   IllegalAccessException,
	                   NoSuchMethodException {
		var    creator = new ProxyCreatorTestImpl(new BesideOriginInfuser(MethodHandles.lookup()));
		Origin proxy;
		Origin origin;
		do {
			proxy  = buildSimpleProxy(MethodHandles.lookup());
			origin = (Origin) creator.getOrigin(proxy);
		} while (proxy.prop == origin.prop);//this test is useless when prop is the same anyways
		creator.sync(proxy);
		assertEquals(
				origin.prop,
				proxy.prop,
				"After syncing all common fields of origin and proxy should be equal"
		);
	}

	@Test
	void setProxyFields() {
		var    creator       = new ProxyCreatorTestImpl(new BesideOriginInfuser(MethodHandles.lookup()));
		Object newFakeOrigin = 198;
		var    fakeProxy     = new FakeProxy();
		creator.setProxyFields(newFakeOrigin, FakeProxy.class, fakeProxy);
		assertNotNull(
				fakeProxy.PROXY_WRAPPER,
				"After setProxyFields the PROXY_WRAPPER should be set"
		);
		assertEquals(
				newFakeOrigin,
				fakeProxy.PROXY_ORIGIN,
				"The 1. parameter should be used as origin"
		);
	}
}