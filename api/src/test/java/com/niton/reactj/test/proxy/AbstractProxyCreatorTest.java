package com.niton.reactj.test.proxy;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.proxy.AbstractProxyCreator;
import com.niton.reactj.api.proxy.InfusionAccessProvider;
import com.niton.reactj.api.proxy.ProxyException;
import com.niton.reactj.api.react.ReactiveWrapper;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AbstractProxyCreatorTest {

    private final static String fakeOrigin = "AJKSD7312";

    public static class ProxyCreatorTestImpl extends AbstractProxyCreator {
        protected ProxyCreatorTestImpl() {
            super(new InfusionAccessProvider() {
                @Override
                public Lookup getLookup(Class<?> originClass) {
                    return null;
                }

                @Override
                public String getPackage(Class<?> originClass) {
                    return "com.niton";
                }
            });
        }

        public static Field getField(Class<?> proxyClass, String field) {
            return getField(proxyClass, new HashMap<>(), field);
        }

        @Override
        //expose for testing
        public Object getOrigin(Object proxy) throws IllegalAccessException {
            return super.getOrigin(proxy);
        }

        @Override
        public Class<?> getProxyClass(Class<?> originClass) {
            return super.getProxyClass(originClass);
        }

        @Override
        protected <T> Class<? extends T> createProxyClass(Class<? extends T> baseClass) {
            return baseClass;
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

    public static class FakeProxy_PROXY$12 {
        private final Object                  PROXY_ORIGIN = fakeOrigin;
        private       ReactiveWrapper<Object> PROXY_WRAPPER;
    }

    public static class UnsafeProxy_PROXY$12 extends FakeProxy_PROXY$12 {
        private final UnsafeProxy_PROXY$12    PROXY_ORIGIN;
        public        Object                  prop;
        private       ReactiveWrapper<Object> PROXY_WRAPPER;

        UnsafeProxy_PROXY$12(UnsafeProxy_PROXY$12 proxy_origin) {PROXY_ORIGIN = proxy_origin;}
    }

    @Test
    void createProxy() {
        var creator = new ProxyCreatorTestImpl();
        assertThrows(
                ProxyException.class,
                () -> creator.getProxyClass(FakeProxy_PROXY$12.class),
                "Creating a proxy class for a proxy should throw an exception"
        );
        assertEquals(ValidBase.class, creator.getProxyClass(ValidBase.class));
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
        var creator = new ProxyCreatorTestImpl();
        assertEquals(fakeOrigin, creator.getOrigin(new FakeProxy()));
    }

    @Test
    void allowsUnsafeProxies() {
        var creator = new ProxyCreatorTestImpl();
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
    void hasDefaultBuilder() {
        var creator = new ProxyCreatorTestImpl();
        assertNotNull(creator.getBuilder(), "A Proxy creator should contain a builder by default");
    }

    @Test
    void sync() {
        var creator = new ProxyCreatorTestImpl();
        var origin  = new UnsafeProxy_PROXY$12(null);
        var proxy   = new UnsafeProxy_PROXY$12(origin);
        proxy.prop  = "794123";
        origin.prop = 66123;
        var toCopy = proxy.prop;
        creator.sync(proxy);
        assertEquals(
                origin.prop, proxy.prop,
                "After syncing all common fields of origin and proxy should be equal"
        );
        assertEquals(
                toCopy, origin.prop,
                "The values should be copied from proxy to origin not the other way around"
        );
    }

    @Test
    void noProxySync() {
        var creator = new ProxyCreatorTestImpl();
        var proxy   = new ValidBase("test");
        assertThrows(
                IllegalArgumentException.class,
                () -> creator.sync(proxy),
                "Syncing a non-proxy should throw an exception"
        );
    }

    @Test
    void setProxyFields() {
        var    creator       = new ProxyCreatorTestImpl();
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