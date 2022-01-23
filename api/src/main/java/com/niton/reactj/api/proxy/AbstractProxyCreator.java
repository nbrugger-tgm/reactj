package com.niton.reactj.api.proxy;

import com.niton.reactj.api.exceptions.ReactiveAccessException;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.react.ReactiveWrapper;
import com.niton.reactj.api.util.ReflectiveUtil;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.niton.reactj.api.proxy.ProxyBuilder.*;

/**
 * A Proxy create is an intermediate layer used for creating proxies. When you invent a new type
 * of proxy you can use this class to offer a creation mechanism. If you for example want to
 * create a MapProxy that can observe changes in a map you should create a {@code MapProxyCreator
 * extends AbstractProxyCreator}
 */
public abstract class AbstractProxyCreator {
    /**
     * The underlying proxy builder implementation to use for the actual proxy creation.
     */
    private final ProxyBuilder            builder;
    private final Map<Class<?>, Class<?>> proxyClasses       = new ConcurrentHashMap<>();
    private final Map<Class<?>, Field>    wrapperFields      = new ConcurrentHashMap<>();
    private final Map<Class<?>, Field>    originFields       = new ConcurrentHashMap<>();
    /**
     * The Infusion type to use, describes the type of the injection for the proxy.
     */
    private final InfusionAccessProvider  accessor;
    private       boolean                 allowUnsafeProxies = false;

    protected AbstractProxyCreator(InfusionAccessProvider accessor) {
        this.accessor = accessor;
        builder       = ProxyBuilder.load(accessor);
    }


    /**
     * Copies values from the proxy to the actual object.
     * This is only needed if you accessed variables without getters or setters
     *
     * @param proxy the proxy to sync with its underlying object
     */
    public void sync(Object proxy) {
        assertIsProxy(proxy);

        try {
            Object origin = getOrigin(proxy);
            for (Field field : origin.getClass().getFields()) {
                syncField(proxy, origin, field);
            }
        } catch (IllegalAccessException e) {
            throw new ProxyException("Syncing origin to proxy failed", e);
        }
    }

    protected void assertIsProxy(Object proxy) {
        if (!proxy.getClass().getName().matches(PROXY_NAME_REGEX)) {
            throw new IllegalArgumentException("sync() requires an proxy");
        }
    }

    protected Object getOrigin(Object proxy) throws IllegalAccessException {
        return getField(proxy.getClass(), originFields, ORIGIN_FIELD).get(proxy);
    }

    protected static Field getField(
            Class<?> proxyClass,
            Map<Class<?>, Field> fieldMap,
            String fieldName
    ) {
        return fieldMap.computeIfAbsent(proxyClass, pc -> {
            try {
                Field field = pc.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                throw new ReactiveException("Can't find wrapper field in proxy", e);
            }
        });
    }

    private void syncField(Object proxy, Object origin, Field field) throws IllegalAccessException {
        if (ReflectiveUtil.isMutableInstanceVar(field)) {
            field.set(origin, field.get(proxy));
        }
    }

    public void setAllowUnsafeProxies(boolean allowUnsafeProxies) {
        this.allowUnsafeProxies = allowUnsafeProxies;
    }

    protected Class<?> getProxyClass(Class<?> originClass) {
        if (!allowsUnsafeProxies())
            verifyOriginClass(originClass);

        assertNotProxy(originClass);

        return proxyClasses.computeIfAbsent(originClass, this::createProxyClass);
    }

    public boolean allowsUnsafeProxies() {
        return allowUnsafeProxies;
    }

    public static <T> void verifyOriginClass(Class<? extends T> originClass) {
        for (Field f : originClass.getFields()) {
            if (ReflectiveUtil.isMutableInstanceVar(f))
                ProxyException.publicFieldException(originClass);
        }
    }

    protected void assertNotProxy(Class<?> originClass) {
        if (originClass.getName().matches(PROXY_NAME_REGEX)) {
            throw ProxyException.doubleProxyException(originClass);
        }
    }

    protected abstract <T> Class<? extends T> createProxyClass(Class<? extends T> baseClass);

    protected Lookup getLookup(Class<?> origin) {
        try {
            return accessor.getLookup(origin);
        } catch (IllegalAccessException e) {
            throw new ReactiveAccessException(e);
        }
    }

    /**
     * Sets the values into a proxy that it needs to operate
     *
     * @param object     the origin object the proxy was created from
     * @param proxyClass the runtime type of the proxy
     * @param proxy      the proxy object to write the fields into
     * @param <T>        the origin type of the proxy
     */
    protected <T> void setProxyFields(T object, Class<?> proxyClass, T proxy) {
        try {
            getField(proxyClass, wrapperFields, WRAPPER_FIELD).set(
                    proxy,
                    new ReactiveWrapper<>(object)
            );
            getField(proxyClass, originFields, ProxyBuilder.ORIGIN_FIELD).set(proxy, object);
        } catch (IllegalAccessException e) {
            //not going to happen
            System.err.println("[reactj-api] This code should never be reached and is a bug in " +
                                       "reactj. Please report it to the reactj developers.");
        }
    }

    public ProxyBuilder getBuilder() {
        return builder;
    }
}
