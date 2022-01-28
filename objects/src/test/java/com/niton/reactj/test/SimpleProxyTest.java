package com.niton.reactj.test;

import com.niton.reactj.api.proxy.ProxyBuilder;
import com.niton.reactj.api.proxy.infusion.BesideOriginInfuser;
import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.react.ReactiveWrapper;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProxyBuilder")
class SimpleProxyTest {
    private boolean called = false;

    @Test
    void createSimpleProxy()
            throws NoSuchMethodException, InvocationTargetException, InstantiationException,
                   IllegalAccessException,
                   NoSuchFieldException {
        var    thisLookup = MethodHandles.lookup();
        Origin origin     = buildSimpleProxy(thisLookup);


        Reactable reactable = (Reactable) origin;
        reactable.reactEvent().listen(() -> called = true);
        called = false;
        origin.doSomething();
        assertTrue(called);
    }

    public static Origin buildSimpleProxy(MethodHandles.Lookup thisLookup)
            throws InstantiationException, IllegalAccessException, InvocationTargetException,
                   NoSuchMethodException,
                   NoSuchFieldException {
        var infus   = new BesideOriginInfuser(thisLookup);
        var builder = ProxyBuilder.load(infus);
        Class<? extends Origin> proxyClass = builder
                .buildProxy(
                        Origin.class,
                        ElementMatchers.any(),
                        ElementMatchers.none()
                )
                .make()
                .load(
                        SimpleProxyTest.class.getClassLoader(),
                        ClassLoadingStrategy.UsingLookup.of(thisLookup)
                )
                .getLoaded();
        Origin origin       = proxyClass.getConstructor().newInstance();
        var    wrapperField = proxyClass.getDeclaredField(ProxyBuilder.WRAPPER_FIELD);
        wrapperField.setAccessible(true);
        wrapperField.set(origin, new ReactiveWrapper<>(null));

        var originField = proxyClass.getDeclaredField(ProxyBuilder.ORIGIN_FIELD);
        originField.setAccessible(true);
        originField.set(origin, new Origin());
        return origin;
    }

    public static class Origin {
        private static int counter = 0;
        public         int prop    = counter++;

        public Origin()           {}

        public void doSomething() {}
    }
}