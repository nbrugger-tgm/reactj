package com.niton.reactj.api.proxy;

import com.niton.reactj.api.annotation.Unreactive;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.proxy.ProxyForwardImpl.Equals;
import com.niton.reactj.api.proxy.ProxyForwardImpl.ToOrigin;
import com.niton.reactj.api.react.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.UsingLookup;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default;
import net.bytebuddy.implementation.*;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.PRIVATE;
import static net.bytebuddy.implementation.DefaultMethodCall.prioritize;
import static net.bytebuddy.implementation.MethodCall.invoke;
import static net.bytebuddy.implementation.MethodDelegation.toField;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class ProxyCreator {
	static final Map<Class<?>, Class<?>>              proxyClasses    = new HashMap<>();
	static final Map<Class<?>, ObjectInstantiator<?>> proxyInitiators = new HashMap<>();
	static final Map<Class<?>, Field>                 wraperFields    = new HashMap<>();
	static final Map<Class<?>, Field>                 originFields    = new HashMap<>();

	public static final String originField  = "PROXY$ORIGIN";
	static final        String wrapperField = "PROXY$WRAPPER_REF";

	static final Method getForwardTargetMethod;
	static final Method cloneMethod;

	private static final Objenesis objenesis = new ObjenesisStd();
	public static boolean allowUnsafeProxies = false;
	private static final String proxySuffix = "PROXY";

	static {
		try {
			getForwardTargetMethod = ReactiveForwarder.class.getDeclaredMethod("getReactableTarget");
			cloneMethod = Object.class.getDeclaredMethod("clone");
		} catch (NoSuchMethodException e) {
			throw new ReactiveException("FATAL: react method not loadable!", e);
		}
	}

	private ProxyCreator() {}

	public static <T extends ProxySubject> T create(T object) {
		Class<? extends ProxySubject> originClass = object.getClass();
		Class<?> proxyClass = proxyClasses.computeIfAbsent(
				originClass,
				ProxyCreator::createProxyClass
		);
		ObjectInstantiator<?> initiator = proxyInitiators.computeIfAbsent(
				proxyClass,
				objenesis::getInstantiatorOf
		);

		@SuppressWarnings("unchecked")
		T proxy = (T) initiator.newInstance();

		try {
			getField(proxyClass, wraperFields, wrapperField).set(proxy, new ReactiveWrapper<>(object));
			getField(proxyClass, originFields, originField).set(proxy, object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			//not going to happen
		}
		return proxy;
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

	public static <T> ReactiveProxy<T> create(T object) {
		Class<?>              originClass = object.getClass();
		Class<?>              proxyClass  = proxyClasses.computeIfAbsent(originClass, ProxyCreator::createProxyClass);
		ObjectInstantiator<?> initiator   = proxyInitiators.computeIfAbsent(proxyClass, objenesis::getInstantiatorOf);

		@SuppressWarnings("unchecked")
		T proxy = (T) initiator.newInstance();
		if(!(proxy instanceof Reactable))
			throw new ReactiveException("Create proxy is not reactive");
		ReactiveWrapper<T> wrapper = new ReactiveWrapper<>(object);
		try {
			getField(proxyClass, wraperFields, wrapperField).set(proxy, wrapper);
			getField(proxyClass, originFields, originField).set(proxy, object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			//not going to happen
		}
		return new ReactiveProxy<>(proxy);
	}

	private static <T> Class<? extends T> createProxyClass(Class<? extends T> originClass) throws ProxyException {
		if(!allowUnsafeProxies)
			verifyOriginClass(originClass);
		if(originClass.getName().endsWith(proxySuffix))
			throw new ProxyException("You can't create a proxy from a proxy",new IllegalArgumentException(originClass.getName()));
		Module module = originClass.getModule();
		Lookup lookup = getLookup(originClass, module);
		var reactiveMethod =
				isDeclaredBy(Reactable.class)
						.or(isOverriddenFrom(Reactable.class));
		var fromObject =
				isDeclaredBy(Object.class)
				.or(isOverriddenFrom(Object.class));
		var unreactive =
				isAnnotatedWith(Unreactive.class)
				.or(fromObject);
		var excluded =
				is(cloneMethod)
				.and(not(isPublic()));
		var proxyClass = new ByteBuddy()
				.subclass(originClass, Default.IMITATE_SUPER_CLASS)
				.implement(ReactiveForwarder.class)
				.name(format("%s_%s",originClass.getName(),proxySuffix))
				.defineField(originField, originClass, PRIVATE)
				.defineField(wrapperField, ReactiveWrapper.class, PRIVATE)

				.method(reactiveMethod)
				.intercept(prioritize(ReactiveForwarder.class))

				.method(is(getForwardTargetMethod))
				.intercept(FieldAccessor.ofField(wrapperField))

				.method(
						not(unreactive)
						.and(not(reactiveMethod))
						.and(not(is(getForwardTargetMethod)))
				)
				.intercept(
						MethodDelegation.to(ToOrigin.class)
				)

				.method(isEquals())
				.intercept(
						MethodDelegation.to(Equals.class)
				)

				.method(
						unreactive
								.and(not(excluded))
								.and(not(isEquals()))
				)
				.intercept(
						MethodCall.invokeSelf()
						          .onField(originField)
						          .withAllArguments()
				)

				.method(excluded)
				.intercept(ExceptionMethod.throwing(CloneNotSupportedException.class))

				.make()
				.load(module.getClassLoader(), UsingLookup.of(lookup));
		try {
			proxyClass.toJar(new File("D:\\Users\\Nils\\Desktop\\proxy.jar"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return proxyClass.getLoaded();
	}

	private static <T> void verifyOriginClass(Class<? extends T> originClass) {
		for(Field f : originClass.getFields())
			if(isMutableInstanceVar(f))
				throw new ReactiveException("Class "+originClass.getName()+" contains public writable instance variables, such classes can't be proxied\n" +
				                            "If possible encapsulate using getters & setters, if not use ProxyCreator.allowUnsafeProxies\n" +
				                            "Be aware that if you use unsafe proxies you need to sync using ProxyCreator.sync()");
	}

	private static boolean isMutableInstanceVar(Field f) {
		return !Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers());
	}

	private static <T> Lookup getLookup(Class<? extends T> originClass, Module module) {
		ProxyCreator.class.getModule().addReads(module);
		Lookup lookup;
		try {
			lookup = MethodHandles.privateLookupIn(originClass, MethodHandles.lookup());
		} catch (IllegalAccessException e) {
			throw new ReactiveException("reactj can't get access to "+module.getName(), e);
		}
		return lookup;
	}

	public static void sync(Object proxy){
		if(!proxy.getClass().getName().endsWith(proxySuffix))
			throw new IllegalArgumentException("sync() requires an proxy");

		try {
			Object origin = getField(proxy.getClass(),originFields,originField).get(proxy);
			for(Field f : origin.getClass().getFields())
				if(isMutableInstanceVar(f)) {
						f.set(origin,f.get(proxy));
				}
		} catch (IllegalAccessException e) {
			throw new ProxyException("Syncing origin to proxy failed",e);
		}
	}
}
