package com.niton.reactj.api.proxy;

import com.niton.reactj.api.exceptions.ReactiveAccessException;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.proxy.infusion.InfusionAccessProvider;
import com.niton.reactj.api.react.ReactiveWrapper;
import com.niton.reactj.utils.reflections.ReflectiveUtil;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.niton.reactj.api.proxy.ProxyBuilder.*;

public abstract class AbstractProxyCreator {
	private final ProxyBuilder            builder;
	private final Map<Class<?>, Class<?>> proxyClasses       = new HashMap<>();
	private final Map<Class<?>, Field>    wrapperFields      = new HashMap<>();
	private final Map<Class<?>, Field>    originFields       = new HashMap<>();
	private final InfusionAccessProvider  accessor;
	private       boolean                 allowUnsafeProxies = false;

	protected AbstractProxyCreator(InfusionAccessProvider accessor) {
		this.accessor = accessor;
		builder       = new ProxyBuilder(accessor);
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
			for (Field f : origin.getClass().getDeclaredFields()) {
				syncField(proxy, origin, f);
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

	private void syncField(Object proxy, Object origin, Field f) throws IllegalAccessException {
		if (ReflectiveUtil.isMutableInstanceVar(f)) {
			f.set(origin, f.get(proxy));
		}
	}

	protected static Field getField(
			Class<?> proxyClass,
			Map<Class<?>, Field> fieldMap,
			String field
	) {
		return fieldMap.computeIfAbsent(proxyClass, pc -> {
			try {
				Field f = pc.getDeclaredField(field);
				f.setAccessible(true);
				return f;
			} catch (NoSuchFieldException e) {
				throw new ReactiveException("Can't find wrapper field in proxy", e);
			}
		});
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

	protected abstract <T> Class<? extends T> createProxyClass(Class<? extends T> aClass);

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
			getField(proxyClass, originFields, ORIGIN_FIELD).set(proxy, object);
		} catch (IllegalAccessException e) {
			//not going to happen
			e.printStackTrace();
		}
	}

	public ProxyBuilder getBuilder() {
		return builder;
	}
}
