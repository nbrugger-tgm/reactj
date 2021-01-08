package com.niton.reactj;

import com.niton.reactj.annotation.Unreactive;
import com.niton.reactj.exceptions.ReactiveException;
import com.niton.reactj.util.ReactiveReflectorUtil;
import javassist.util.proxy.ProxyFactory;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The base class to make a Object reactive (usable in ReactiveComponents).
 *
 * The most common way to use this component is by extending it and call {@link ReactiveObject#react()} whenever needed
 */
public class ReactiveObject implements Reactable {
	private final Object store;

	@Unreactive
	protected final List<com.niton.reactj.Observer<?>> listeners = new ArrayList<>();

	/**
	 * Creates a Reactive Object that forwards calls to the given object
	 * @param obj the object to forward calls to
	 */
	public ReactiveObject(Object obj) {
		store = obj;
	}

	/**
	 * Only use this constructor when extending from this class
	 */
	public ReactiveObject(){
		store = this;
	}

	/**
	 * its the same as {@link ReactiveProxy#create(Class, Object...)}
	 */
	public static <C> ReactiveProxy<C> create(Class<C> type, Object... constructorParameters)
	throws
	ReactiveException {
		Class<?>[] paramTypes = Arrays.stream(constructorParameters)
		                              .map(Object::getClass)
		                              .toArray(Class[]::new);

		Class<?>[] unboxedParamTypes = unboxTypes(paramTypes);

		C real = tryInstantiation(type,
		                          paramTypes,
		                          unboxedParamTypes,
		                          constructorParameters);
		ReactiveProxy<C> model = new ReactiveProxy<>(real);
		C wrapped = constructProxy(type,
		                           paramTypes,
		                           unboxedParamTypes,
		                           model,
		                           constructorParameters);
		model.setProxy(wrapped);
		return model;
	}

	private static <C> C constructProxy(Class<C> type,
	                                    Class<?>[] paramTypes,
	                                    Class<?>[] unboxedParamTypes,
	                                    ReactiveProxy<C> model,
	                                    Object[] constructorParameters) {
		C wrapped = null;

		ProxyFactory factory = new ProxyFactory();
		factory.setSuperclass(type);
		try {
			try {
				wrapped = (C) factory.create(paramTypes, constructorParameters, model);
			} catch (NoSuchMethodException e) {
				wrapped = (C) factory.create(unboxedParamTypes, constructorParameters, model);
			}
		} catch (
				IllegalAccessException | InstantiationException |
						InvocationTargetException | NoSuchMethodException e
		) {
			handle(type, unboxedParamTypes, e);
		}
		return wrapped;
	}

	/**
	 * Deal with the error
	 * @param exception the exception to deal with
	 */
	private static <C> void handle (
			Class<C> type,
			Class<?>[] types,
			Exception exception
	) throws ReactiveException {
		if (exception instanceof NoSuchMethodException) {
			throw constructorNotFound(type, types);
		} else {
			throw constructionException(type, exception);
		}
	}


	private static <C> C tryInstantiation(
			Class<C> type,
			Class<?>[] paramTypes,
			Class<?>[] unboxedParamTypes,
			Object[] parameters
	) {
		try {
			C real = null;
			if (parameters.length == 0) {
				return type.newInstance();
			}

			try {
				real = instanciate(type, paramTypes, parameters);
			} catch (NoSuchMethodException e) {
				//try again with unboxed types
				real = instanciate(type, unboxedParamTypes, parameters);
			}

			return real;
		} catch (
				InstantiationException | InvocationTargetException |
						IllegalAccessException | NoSuchMethodException e) {
			handle(type, unboxedParamTypes, e);
		}
		//should not be reached
		return null;
	}

	private static <C> ReactiveException constructorNotFound(Class<C> type, Class<?>[] paramTypes) {
		return new ReactiveException(
				String.format("No constructor(%s) found in class %s",
				              Arrays.stream(paramTypes)
				                    .map(Class::getSimpleName)
				                    .collect(Collectors.joining(", ")),
				              type.getSimpleName()));
	}

	private static <C> C instanciate(Class<C> type, Class<?>[] types, Object[] params)
	throws
	NoSuchMethodException,
	IllegalAccessException,
	InvocationTargetException,
	InstantiationException {
		Constructor<C> constructor = type.getConstructor(types);
		constructor.setAccessible(true);
		return constructor.newInstance(params);
	}

	private static <C> ReactiveException constructionException(
			Class<C> type,
			Exception cause
	) {
		ReactiveException ex = new ReactiveException(
				String.format("Couldn't construct %s",
				              type.getSimpleName()));
		ex.initCause(cause);
		return ex;
	}

	private static Class<?>[] unboxTypes(Class<?>[] paramTypes) {
		return Arrays
				.stream(paramTypes)
				.map(c -> MethodType.methodType(c).unwrap().returnType())
				.toArray(Class[]::new);
	}

	@Override
	public void bind(Observer<?> observer) {
		listeners.add(observer);
	}

	@Override
	public Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(store);
	}

	@Override
	public void unbind(com.niton.reactj.Observer<?> observer) {
		listeners.remove(observer);
	}

	@Override
	public void react() {
		listeners.forEach(Observer::update);
	}

	@Override
	public void react(String name, Object obj) {
		listeners.forEach(l -> l.update(Collections.singletonMap(name, obj)));
	}

	@Override
	public void set(String property, Object value) throws Throwable {
		ReactiveReflectorUtil.updateField(store, property, value);
	}

	@Override
	public void unbindAll() {
		listeners.clear();
	}
}
