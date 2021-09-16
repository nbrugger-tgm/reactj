package com.niton.reactj.api.proxy;

import com.niton.reactj.api.ReactiveProxy;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.util.ReactiveReflectorUtil;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static com.niton.reactj.api.exceptions.ReactiveException.constructionException;
import static com.niton.reactj.api.exceptions.ReactiveException.constructorNotFound;
import static java.lang.String.format;

public class ProxyCreator {

	/**
	 * Works similar ro {@link #wrapper(Class, Object...)} but takes advantage over "Pseudo Proxies"
	 * (https://github.com/nbrugger-tgm/reactj/issues/31)<br>
	 * Described more detailed here : https://github.com/nbrugger-tgm/reactj/wiki/Models#proxysubject
	 *
	 * @param type              the class to create a proxy for
	 * @param constructorParams constructor parameters used to build the object
	 * @param <C>               the type of the object to create a proxy for
	 *
	 * @return an instance of {@code <C>} but within a proxy
	 *
	 * @throws ReactiveException if stuff goes wrong, many things can cause this. Mostly reflective missfunction
	 */
	public static <C extends ProxySubject> C subject(Class<C> type, Object... constructorParams)
			throws
			ReactiveException {
		return wrapper((Class<? extends C>) type, constructorParams).getObject();
	}

	/**
	 * Create a new ReactiveProxy from a certain class.
	 * <p>
	 * A Proxy manages reactivity automatically. So no need to extend ReactiveObject.<br>
	 * This function uses the constructor of the given type. So the type <b>MUST</b> have an accessible constructor. The
	 * constructor is allowed to have arguments
	 *
	 * @param type              The type the ReactiveProxy should emulate (e.g. Person.class)
	 * @param constructorParams the arguments to pass to the constructor
	 * @param <C>               the type the Proxy will emulate
	 *
	 * @return the created proxy
	 */
	public static <C> ReactiveProxy<C> wrapper(Class<C> type, Object... constructorParams)
			throws
			ReactiveException {
		checkInstantiatable(type);
		Class<?>[] paramTypes = Arrays.stream(constructorParams)
		                              .map(Object::getClass)
		                              .toArray(Class[]::new);

		Class<?>[] unboxedParamTypes = ReactiveReflectorUtil.unboxTypes(paramTypes);

		C real = tryInstantiation(
				type,
				paramTypes,
				unboxedParamTypes,
				constructorParams
		);

		return wrap(real, type, paramTypes, unboxedParamTypes, constructorParams);
	}

	/**
	 * @throws IllegalArgumentException if {@code type} is abstract or an interface
	 */
	private static <C> void checkInstantiatable(Class<C> type) {
		if (type.isInterface()) {
			throw new IllegalArgumentException(
					format("'%s' is an interface and therefore not instantiatable", type.getSimpleName())
			);
		}
		if (Modifier.isAbstract(type.getModifiers())) {
			throw new IllegalArgumentException(
					format("'%s' is an abstract class and therefore not instantiatable", type.getSimpleName())
			);
		}
	}

	private static <C> C tryInstantiation(
			Class<C> type,
			Class<?>[] paramTypes,
			Class<?>[] unboxedParamTypes,
			Object... parameters
	) throws ReactiveException {
		try {
			if (parameters.length == 0) {
				return type.newInstance();
			}

			C real;
			try {
				real = instantiate(type, paramTypes, parameters);
			} catch (NoSuchMethodException e) {
				//try again with unboxed types
				real = instantiate(type, unboxedParamTypes, parameters);
			}

			return real;
		} catch (Exception e) {
			throw handle(type, unboxedParamTypes, e);
		}
	}

	private static <C> ReactiveProxy<C> wrap(
			C original,
			Class<C> type,
			Class<?>[] paramTypes,
			Class<?>[] unboxedParamTypes,
			Object[] constructorParams
	) {
		ReactiveProxyEngine<C> model = new ReactiveProxyEngine<>(original);
		C proxy = constructProxy(
				type,
				paramTypes,
				unboxedParamTypes,
				model,
				constructorParams
		);
		return new ReactiveProxy<>(model.getWrapper(), proxy);
	}

	private static <C> C instantiate(Class<C> type, Class<?>[] types, Object[] params)
			throws
			NoSuchMethodException,
			IllegalAccessException,
			InvocationTargetException,
			InstantiationException {
		Constructor<C> constructor = type.getConstructor(types);
		constructor.setAccessible(true);
		return constructor.newInstance(params);
	}

	/**
	 * Deal with the error
	 *
	 * @param exception the exception to deal with
	 */
	private static <C> ReactiveException handle(
			Class<C> type,
			Class<?>[] types,
			Exception exception
	) throws ReactiveException {
		if (exception instanceof NoSuchMethodException) {
			return constructorNotFound(type, types);
		} else {
			return constructionException(type, exception);
		}
	}

	@SuppressWarnings("unchecked")
	private static <C> C constructProxy(
			Class<C> type,
			Class<?>[] paramTypes,
			Class<?>[] unboxedParamTypes,
			ReactiveProxyEngine<C> handler,
			Object... constructorParams
	) {
		ProxyFactory factory = new ProxyFactory();
		factory.setSuperclass(type);
		try {
			C wrapped;
			try {
				wrapped = (C) factory.create(paramTypes, constructorParams, handler);
			} catch (NoSuchMethodException e) {
				wrapped = (C) factory.create(unboxedParamTypes, constructorParams, handler);
			}
			return wrapped;
		} catch (
				IllegalAccessException | InstantiationException |
						InvocationTargetException | NoSuchMethodException e
		) {
			throw handle(type, unboxedParamTypes, e);
		}
	}

	/**
	 * Creates a proxy similar to {@link #subject(Class, Object...)} but from a "live" object
	 *
	 * @param original the object to create the proxy for
	 *
	 * @return the wrapped object
	 */
	public static <C extends ProxySubject> C wrapSubject(C original, Object... constructorParams) {
		return innerWrap(original, constructorParams).getObject();
	}

	/**
	 * tries to wrap a live object
	 *
	 * @param original          the object to wrap
	 * @param constructorParams parameters used to create proxy
	 * @param <C>               the type of the object to wrap
	 *
	 * @return the wraped object as proxy
	 */
	private static <C> ReactiveProxy<C> innerWrap(C original, Object... constructorParams) {
		@SuppressWarnings("unchecked")
		Class<C> type = (Class<C>) original.getClass();
		Class<?>[] paramTypes = Arrays.stream(constructorParams)
		                              .map(Object::getClass)
		                              .toArray(Class[]::new);
		Class<?>[] unboxedParamTypes = ReactiveReflectorUtil.unboxTypes(paramTypes);
		return wrap(original, type, paramTypes, unboxedParamTypes, constructorParams);
	}

	/**
	 * Creates a proxy similar to {@link #wrapper(Class, Object...)} but from a "live object
	 * instead of creating a new one
	 *
	 * @param original          the object to wrap with the proxy
	 * @param constructorParams the parameters for the construction of the proxy. (must match a constructor from {@code
	 *                          <C>}
	 * @param <C>               the type to create the proxy for
	 *
	 * @return a reactive proxy covering the original object
	 */
	public static <C> ReactiveProxy<C> wrap(C original, Object... constructorParams) {
		return innerWrap(original, constructorParams);
	}
}
