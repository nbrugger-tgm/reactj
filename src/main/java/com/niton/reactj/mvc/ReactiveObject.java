package com.niton.reactj.mvc;

import com.niton.reactj.Observer;
import com.niton.reactj.Reactable;
import com.niton.reactj.ReactiveProxy;
import com.niton.reactj.ReactiveReflectorUtil;
import com.niton.reactj.annotation.Unreactive;
import com.niton.reactj.exceptions.ReactiveException;
import javassist.util.proxy.ProxyFactory;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ReactiveObject implements Reactable {
	@Unreactive
	protected final List<com.niton.reactj.Observer<?>> listeners = new ArrayList<>();

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
		ReactiveModel<C> model = new ReactiveModel<>(real);
		C wrapped = constructProxy(type,
		                           paramTypes,
		                           unboxedParamTypes,
		                           model,
		                           constructorParameters);

		return new ReactiveProxy<>(wrapped, model);
	}

	private static <C> C constructProxy(Class<C> type,
	                                    Class<?>[] paramTypes,
	                                    Class<?>[] unboxedParamTypes,
	                                    ReactiveModel<C> model,
	                                    Object[] constructorParameters) {
		C wrapped;

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
			return handle(type, unboxedParamTypes, e);
		}
		return wrapped;
	}

	private static <C> C handle(
			Class<C> type,
			Class<?>[] types,
			Exception exception
	) {
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
			return handle(type, unboxedParamTypes, e);
		}
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

	public void bind(com.niton.reactj.Observer<?> observer) {
		listeners.add(observer);
	}

	@Override
	public Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(this);
	}

	public void unbind(com.niton.reactj.Observer<?> observer) {
		listeners.remove(observer);
	}

	public void react() {
		listeners.forEach(Observer::update);
	}

	public void react(String name, Object obj) {
		listeners.forEach(l -> l.update(Collections.singletonMap(name, obj)));
	}

	@Override
	public void set(String property, Object value) throws Throwable {
		ReactiveReflectorUtil.updateField(this, property, value);
	}

	@Override
	public void unbindAll() {
		listeners.clear();
	}
}
