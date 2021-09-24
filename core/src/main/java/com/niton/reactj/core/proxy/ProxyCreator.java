package com.niton.reactj.core.proxy;

import com.niton.reactj.api.exceptions.ReactiveAccessException;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.proxy.ProxyException;
import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.react.ReactiveForwarder;
import com.niton.reactj.core.annotation.Unreactive;
import com.niton.reactj.core.react.ReactiveProxy;
import com.niton.reactj.core.react.ReactiveWrapper;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class ProxyCreator {
	public static final ProxyCreator INSTANCE     = new ProxyCreator();
	static final        String       ORIGIN_FIELD = "PROXY$ORIGIN";
	static final        String       WRAPPER_REF  = "PROXY$WRAPPER_REF";
	static final        String       PROXY_SUFFIX = "PROXY";
	static final        Method       getForwardTargetMethod;
	static final        Method       cloneMethod;

	static {
		try {
			getForwardTargetMethod = ReactiveForwarder.class.getDeclaredMethod("getReactableTarget");
			cloneMethod = Object.class.getDeclaredMethod("clone");
		} catch (NoSuchMethodException e) {
			throw new ReactiveException("FATAL: react method not loadable!", e);
		}
	}

	private final Objenesis                            objenesis          = new ObjenesisStd();
	private final ProxyBuilder                         builder            = new ProxyBuilder();
	private final Map<Class<?>, Class<?>>              proxyClasses       = new HashMap<>();
	private final Map<Class<?>, ObjectInstantiator<?>> proxyInitiators    = new HashMap<>();
	private final Map<Class<?>, Field>                 wrapperFields      = new HashMap<>();
	private final Map<Class<?>, Field>                 originFields       = new HashMap<>();
	private       boolean                              allowUnsafeProxies = false;

	public boolean isAllowUnsafeProxies() {
		return allowUnsafeProxies;
	}

	public void setAllowUnsafeProxies(boolean allowUnsafeProxies) {
		this.allowUnsafeProxies = allowUnsafeProxies;
	}

	/**
	 * Copies values from the proxy to the actual object.
	 * This is only needed if you accessed variables without getters or setters
	 *
	 * @param proxy the proxy to sync with its underlying object
	 */
	public void sync(Object proxy) {
		if (!proxy.getClass().getName().endsWith(PROXY_SUFFIX))
			throw new IllegalArgumentException("sync() requires an proxy");

		try {
			Object origin = getOrigin(proxy);
			for (Field f : origin.getClass().getFields())
				if (isMutableInstanceVar(f)) {
					f.set(origin, f.get(proxy));
				}
		} catch (IllegalAccessException e) {
			throw new ProxyException("Syncing origin to proxy failed", e);
		}
	}

	private Object getOrigin(Object proxy) throws IllegalAccessException {
		return getField(proxy.getClass(), originFields, ORIGIN_FIELD).get(proxy);
	}

	private static boolean isMutableInstanceVar(Field f) {
		return !Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers());
	}

	static Field getField(Class<?> proxyClass, Map<Class<?>, Field> fieldMap, String field) {
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

	public <T extends ProxySubject> T create(T object) {
		return createProxy(object);
	}

	private <T> T createProxy(T object) {
		Class<?> originClass = object.getClass();
		Class<?> proxyClass = proxyClasses.computeIfAbsent(originClass, this::createProxyClass);
		ObjectInstantiator<?> initiator = proxyInitiators.computeIfAbsent(proxyClass, objenesis::getInstantiatorOf);

		@SuppressWarnings("unchecked")
		T proxy = (T) initiator.newInstance();

		setProxyFields(object, proxyClass, proxy);
		copyFinalFields(proxy, object);
		return proxy;
	}

	private <T> Class<? extends T> createProxyClass(Class<? extends T> originClass) throws ProxyException {
		if (!allowUnsafeProxies)
			verifyOriginClass(originClass);

		if (originClass.getName().endsWith(PROXY_SUFFIX))
			throw ReactiveException.doubleProxyException(originClass);

		Module module = originClass.getModule();
		Lookup lookup = getLookup(originClass, module);

		var reactiveMethod = isDeclaredBy(Reactable.class).or(isOverriddenFrom(Reactable.class));
		var fromObject = isDeclaredBy(Object.class).or(isOverriddenFrom(Object.class));
		var unreactive = isAnnotatedWith(Unreactive.class).or(fromObject);
		var excluded = is(cloneMethod).and(not(isPublic()));

		return builder.buildProxie(originClass, reactiveMethod, unreactive, excluded, module, lookup);
	}

	/**
	 * Sets the values into a proxy that it needs to operate
	 *
	 * @param object     the origin object the proxie was created from
	 * @param proxyClass the runtime type of the proxy
	 * @param proxy      the proxy object to write the fields into
	 * @param <T>        the origin type of the proxy
	 */
	private <T> void setProxyFields(T object, Class<?> proxyClass, T proxy) {
		try {
			getField(proxyClass, wrapperFields, WRAPPER_REF).set(proxy, new ReactiveWrapper<>(object));
			getField(proxyClass, originFields, ORIGIN_FIELD).set(proxy, object);
		} catch (IllegalAccessException e) {
			//not going to happen
			e.printStackTrace();
		}
	}

	/**
	 * Copies the values of public final fields from origin to proxy
	 */
	private <T> void copyFinalFields(T proxy, T origin) {
		Arrays.stream(proxy.getClass().getFields())//just public ones
				.filter(f -> Modifier.isFinal(f.getModifiers()))//just final ones
				.forEach(f -> copyFinalField(f, proxy, origin));
	}

	private static <T> void verifyOriginClass(Class<? extends T> originClass) {
		for (Field f : originClass.getFields())
			if (isMutableInstanceVar(f))
				publicFieldException(originClass);
	}

	private static <T> Lookup getLookup(Class<? extends T> originClass, Module module) {
		ProxyCreator.class.getModule().addReads(module);
		Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(originClass, MethodHandles.lookup());
		} catch (IllegalAccessException e) {
			throw new ReactiveAccessException(e);
		}
		return lookup;
	}

	private <T> void copyFinalField(Field f, T proxy, T origin) {
		try {
			setFinal(f, proxy, f.get(origin));
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new ReactiveException("Couldn't copy final field", e);
		}
	}

	private static void publicFieldException(Class<?> originClass) {
		throw new ReactiveException(
				"Class " + originClass.getName() +
						" contains public writable instance variables, such classes can't be proxied\n" +
						"If possible encapsulate using getters & setters, if not use ProxyCreator.allowUnsafeProxies\n" +
						"Be aware that if you use unsafe proxies you need to sync using ProxyCreator.sync()"
		);
	}

	/**
	 * THIS IS PURE EVIL ... but necessary
	 */
	private static void setFinal(Field field, Object target, Object newValue)
			throws NoSuchFieldException, IllegalAccessException {
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(target, newValue);
	}

	public <T> ReactiveProxy<T> create(T object) {
		return new ReactiveProxy<>(createProxy(object));
	}

}
