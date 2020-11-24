package com.niton.reactj;

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
	protected final List<ReactiveController<?>> listeners = new ArrayList<>();

	public static <C> ReactiveProxy<C> create(Class<C> type, Object... constructorParameters) throws ReactiveException {
		Class<?>[] paramTypes = Arrays.stream(constructorParameters).map(Object::getClass).toArray(Class[]::new);
		Class<?>[] unboxedParamTypes = unboxTypes(paramTypes);

		C real = tryInstantiation(type, paramTypes, unboxedParamTypes, constructorParameters);
		ReactiveModel<C> model = new ReactiveModel<>(real);
		C wrapped = constructProxy(type, paramTypes, unboxedParamTypes, model, constructorParameters);

		return new ReactiveProxy<>(wrapped, model);
	}

	private static <C> C constructProxy(Class<C> type, Class<?>[] paramTypes, Class<?>[] unboxedParamTypes, ReactiveModel<C> model, Object[] constructorParameters) {
		C wrapped;
		ProxyFactory factory = new ProxyFactory();
		factory.setSuperclass(type);
		try {
			try {
				wrapped = (C) factory.create(paramTypes, constructorParameters, model);
			}catch (NoSuchMethodException e){
				wrapped = (C) factory.create(unboxedParamTypes, constructorParameters, model);
			}
		} catch (
				IllegalAccessException | InstantiationException |
				InvocationTargetException | NoSuchMethodException e
		) {
			return handle(type,constructorParameters,unboxedParamTypes,e);
		}
		return wrapped;
	}

	private static <C> C handle(
			Class<C> type,
			Object[] constructorParameters,
			Class<?>[] types,
			ReflectiveOperationException e
	) {
		if(e instanceof NoSuchMethodException){
			throw constructorNotFound(type,types);
		}else{
			throw constructionException(type,e);
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
			if(parameters.length == 0){
				return type.newInstance();
			}

			try {
				real = instanciate(type, paramTypes, parameters);
			} catch (NoSuchMethodException e) {
				//try again with unboxed types
				real = instanciate(type, unboxedParamTypes, parameters);
			}

			return real;
		}
		catch (
				InstantiationException | InvocationTargetException |
				IllegalAccessException | NoSuchMethodException e
		) {
			return handle(type,parameters,unboxedParamTypes,e);
		}
	}

	private static <C> ReactiveException constructorNotFound(Class<C> o, Class<?>[] paramTypes) {
		return new ReactiveException(
				"No constructor(" +
				Arrays.stream(paramTypes)
						.map(Class::getSimpleName)
						.collect(Collectors.joining(", ")) +
				") found in class " + o.getSimpleName());
	}

	private static <C> C instanciate
			(Class<C> type,Class<?>[] types,Object[] params)
			throws
			NoSuchMethodException,
			IllegalAccessException,
			InvocationTargetException,
			InstantiationException
	{
		Constructor<C> constructor = type.getConstructor(types);
		constructor.setAccessible(true);
		return constructor.newInstance(params);
	}

	private static <C> ReactiveException constructionException(
			Class<C> o,
			ReflectiveOperationException e
	) {
		ReactiveException exception = new ReactiveException("Couldn't construct " + o.getSimpleName());
		exception.initCause(e);
		return exception;
	}

	private static Class<?>[] unboxTypes(Class<?>[] paramTypes) {
		return Arrays
				.stream(paramTypes)
				.map(c -> MethodType.methodType(c).unwrap().returnType())
				.toArray(Class[]::new);
	}

	public void bind(ReactiveController<?> c) {
		listeners.add(c);
	}

	@Override
	public Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(this);
	}

	public void unbind(ReactiveController<?> c) {
		listeners.remove(c);
	}

	public void react() {
		listeners.forEach(ReactiveController::modelChanged);
	}

	public void react(String name, Object obj) {
		listeners.forEach(l -> l.modelChanged(Collections.singletonMap(name, obj)));
	}

	@Override
	public void set(String property, Object value) throws Throwable {
		ReactiveReflectorUtil.updateField(this, property, value);
	}
}
