package com.niton.reactj.api.proxy;

import com.niton.reactj.api.annotation.Unreactive;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.react.ReactiveWrapper;
import com.niton.reactj.event.GenericEventManager;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder.InjectionStrategy;
import net.bytebuddy.agent.builder.AgentBuilder.InjectionStrategy.UsingReflection;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.UsingLookup;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.InjectionClassLoader.Strategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import org.apache.commons.lang3.ClassLoaderUtils;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisBase;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.basic.ObjectInputStreamInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.lang.reflect.Modifier.PRIVATE;
import static net.bytebuddy.implementation.MethodCall.invoke;
import static net.bytebuddy.implementation.MethodDelegation.toField;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class ProxyCreator {
	private ProxyCreator(){}

	private static final Map<Class<?>, Class<?>>           proxyClasses    = new HashMap<>();
	private static final Map<Class<?>, ObjectInstantiator<?>> proxyInitiators = new HashMap<>();
	private static final Map<Class<?>, Field> wraperFields = new HashMap<>();
	private static final Map<Class<?>, Field> originFields = new HashMap<>();
	private static final Map<Class<?>, Field> eventFields = new HashMap<>();
	private static final Map<Module, ClassLoader> proxyLoaders = new HashMap<>();
	private static final String                            originField     = "PROXY$ORIGIN";
	private static final String                  eventField   = "PROXY$EVENT_LISTENER";
	private static final String                  wrapperField = "PROXY$WRAPPER_REF";
	private static final Method reactMethod;
	private static final Method eventMethod;
	private static final Objenesis objenesis = new ObjenesisStd();
	static {
		try {
			reactMethod = Reactable.class.getMethod("react");
			eventMethod = Reactable.class.getMethod("reactEvent");
		} catch (NoSuchMethodException e) {
			throw new ReactiveException("FATAL: react method not loadable!",e);
		}
	}

	public static <T extends ProxySubject> T create(T object){
		Class<? extends ProxySubject> originClass = object.getClass();
		Class<?> proxyClass = proxyClasses.computeIfAbsent(originClass, ProxyCreator::createSubjectProxyClass);
		ObjectInstantiator<?> initiator = proxyInitiators.computeIfAbsent(proxyClass, objenesis::getInstantiatorOf);

		@SuppressWarnings("unchecked")
		T proxy = (T) initiator.newInstance();

		try {
			getField(proxyClass,eventFields,eventField).set(proxy, new GenericEventManager());
			getField(proxyClass,originFields,originField).set(proxy, object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			//not going to happen
		}
		return proxy;
	}

	public static <T> ReactiveWrapper<T> create(T object){
		Class<?> originClass = object.getClass();
		Class<?> proxyClass = proxyClasses.computeIfAbsent(originClass, ProxyCreator::createProxyClass);
		ObjectInstantiator<?> initiator = proxyInitiators.computeIfAbsent(proxyClass, objenesis::getInstantiatorOf);

		@SuppressWarnings("unchecked")
		T proxy = (T) initiator.newInstance();

		ReactiveWrapper<T> wrapper = new ReactiveWrapper<>(proxy);
		try {
			getField(proxyClass,wraperFields,wrapperField).set(proxy, wrapper);
			getField(proxyClass,originFields,originField).set(proxy, object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			//not going to happen
		}
		return wrapper;
	}

	private static Field getField(Class<?> proxyClass, Map<Class<?>,Field> fieldMap, String field) {
		return fieldMap.computeIfAbsent(proxyClass, pc -> {
			Field f;
			try {
				f = pc.getDeclaredField(field);
			} catch (NoSuchFieldException e) {
				throw new ReactiveException("Can't find wrapper field in proxy", e);
			}
			f.setAccessible(true);
			return f;
		});
	}

	private static<T> Class<? extends T> createProxyClass(Class<? extends T> originClass){
		Module module = originClass.getModule();
		ProxyCreator.class.getModule().addReads(module);
		Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(originClass, MethodHandles.lookup());
		} catch (IllegalAccessException e) {
			throw new ReactiveException("YEE",e);
		}

		return new ByteBuddy()
			.subclass(originClass, Default.IMITATE_SUPER_CLASS)
			.suffix("_PROXY")
			.defineField(originField, originClass, PRIVATE)
			.defineField(wrapperField,ReactiveWrapper.class,PRIVATE)
			.method(
					not(isAnnotatedWith(Unreactive.class))
					.and(not(isDeclaredBy(Object.class)))
					.and(returns(Void.TYPE))
			)
				.intercept(
						MethodDelegation.toField(originField)
						                .andThen(invoke(reactMethod).onField(wrapperField))
				)
			.make()
			.load(module.getClassLoader(), UsingLookup.of(lookup))
			.getLoaded();
	}
	private static ClassLoader createOpenLoader(Module module){
		return new ByteArrayClassLoader(null,Map.of());
	}
	private static<T> Class<? extends T> createSubjectProxyClass(Class<? extends T> originClass) {
		return new ByteBuddy()
				.subclass(originClass, Default.IMITATE_SUPER_CLASS)
				.suffix("_SUBJECT_PROXY")
				.defineField(originField, originClass, PRIVATE)
				.defineField(eventField, GenericEventManager.class, PRIVATE)
				.method(isDeclaredBy(ProxySubject.class))
					.intercept(toField(originField))
				.method(
						not(isAnnotatedWith(Unreactive.class))
						.and(not(isDeclaredBy(ProxySubject.class)))
						.and(not(isDeclaredBy(Object.class)))
						.and(not(is(reactMethod)))
				)
					.intercept(
							MethodDelegation.toField(originField)
							.andThen(invoke(reactMethod).onSuper())
					)
				.method(is(eventMethod))
					.intercept(FieldAccessor.ofField(eventField))
				.make()
				.load(originClass.getClassLoader())
				.getLoaded();
	}
}
