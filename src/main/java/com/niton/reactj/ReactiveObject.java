package com.niton.reactj;

import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.annotation.Unreactive;
import com.niton.reactj.exceptions.ReactiveException;
import com.niton.reactj.mvc.EventManager;
import com.niton.reactj.mvc.GenericEventManager;
import com.niton.reactj.observers.ObjectObserver;
import com.niton.reactj.observers.ObjectObserver.PropertyObservation;
import com.niton.reactj.proxy.ProxySubject;
import com.niton.reactj.proxy.ReactiveProxy;
import com.niton.reactj.util.ReactiveReflectorUtil;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.niton.reactj.exceptions.ReactiveException.constructionException;
import static com.niton.reactj.exceptions.ReactiveException.constructorNotFound;

/**
 * The base class to make a Object reactive (usable in ReactiveComponents).
 * <p>
 * The most common way to use this component is by extending it and call {@link ReactiveObject#react()} whenever needed
 */
public class ReactiveObject implements Reactable {
	@Override
	public GenericEventManager reactEvent() {
		return thisWrapper.reactEvent();
	}

	private final ReactiveWrapper<ReactiveObject> thisWrapper = new ReactiveWrapper<>(this);
	/**
	 * Only use this constructor when extending from this class
	 */
	protected ReactiveObject() {}

	/**
	 * alias to {@link ReactiveProxy#createProxy(Class, Object...)}
	 */
	public static <C> ReactiveProxy<C> createProxy(Class<C> type, Object... constructorParams)
	throws
	ReactiveException {
		checkInstantiatable(type);
		Class<?>[] paramTypes = Arrays.stream(constructorParams)
		                              .map(Object::getClass)
		                              .toArray(Class[]::new);

		Class<?>[] unboxedParamTypes = ReactiveReflectorUtil.unboxTypes(paramTypes);

		C real = tryInstantiation(type,
		                          paramTypes,
		                          unboxedParamTypes,
		                          constructorParams);

		return wrap(real, type, paramTypes, unboxedParamTypes, constructorParams);
	}

	/**
	 * @throws IllegalArgumentException if {@code type} is abstract or an interface
	 */
	private static <C> void checkInstantiatable(Class<C> type) {
		if(type.isInterface()) {
			throw new IllegalArgumentException(
				String.format("\"%s\" is an interface and not instantiatable", type.getSimpleName())
			);
		}
		if(Modifier.isAbstract(type.getModifiers())) {
			throw new IllegalArgumentException(
				String.format(
					"\"%s\" is an abstract class and therefore not instantiatable",
					type.getSimpleName()
				)
			);
		}
	}

	/**
	 * alias to {@link ReactiveProxy#create(Class, Object...)}
	 *
	 * @see ReactiveProxy#create(Class, Object...)
	 */
	public static <C extends ProxySubject> C create(Class<C> type, Object... constructorParams)
	throws
	ReactiveException {
		return createProxy((Class<? extends C>) type, constructorParams).getObject();
	}

	/**
	 * Creates a proxy similar to {@link ReactiveProxy#create(Class, Object...)} but from a "live" object
	 *
	 * @param original the object to create the proxy for
	 * @return the wrapped object
	 */
	public static <C extends ProxySubject> C wrap(C original, Object... constructorParams) {
		return innerWrap(original, constructorParams).getObject();
	}

	/**
	 * Creates a proxy similar to {@link ReactiveProxy#createProxy(Class, Object...)} but from a "live object instead of creating a new one
	 *
	 * @param original          the object to wrap with the proxy
	 * @param constructorParams the parameters for the construction of the proxy. (must match a constructor from {@code <C>}
	 * @param <C>               the type to create the proxy for
	 * @return a reactive proxy covering the original object
	 */
	public static <C> ReactiveProxy<C> wrap(C original, Object... constructorParams) {
		return innerWrap(original, constructorParams);
	}

	/**
	 * tries to wrap a live object
	 *
	 * @param original          the object to wrap
	 * @param constructorParams parameters used to create proxy
	 * @param <C>               the type of the object to wrap
	 * @return the wraped object as proxy
	 */
	private static <C> ReactiveProxy<C> innerWrap(C original, Object... constructorParams) {
		Class<C> type = (Class<C>) original.getClass();
		Class<?>[] paramTypes = Arrays.stream(constructorParams)
		                              .map(Object::getClass)
		                              .toArray(Class[]::new);
		Class<?>[] unboxedParamTypes = ReactiveReflectorUtil.unboxTypes(paramTypes);

		return wrap(original, type, paramTypes, unboxedParamTypes, constructorParams);
	}

	private static <C> ReactiveProxy<C> wrap(
		C original,
		Class<C> type,
		Class<?>[] paramTypes,
		Class<?>[] unboxedParamTypes,
		Object[] constructorParams
	) {
		ReactiveProxy<C> model = new ReactiveProxy<>(original);
		C wrapped = constructProxy(type,
		                           paramTypes,
		                           unboxedParamTypes,
		                           model,
		                           constructorParams);
		model.setProxy(wrapped);
		return model;
	}


	private static <C> C constructProxy(Class<C> type,
	                                    Class<?>[] paramTypes,
	                                    Class<?>[] unboxedParamTypes,
	                                    ReactiveProxy<C> model,
	                                    Object... constructorParams) {


		ProxyFactory factory = new ProxyFactory();
		factory.setSuperclass(type);
		try {
			C wrapped;
			try {
				wrapped = (C) factory.create(paramTypes, constructorParams, model);
			} catch(NoSuchMethodException e) {
				wrapped = (C) factory.create(unboxedParamTypes, constructorParams, model);
			}
			return wrapped;
		} catch(
			IllegalAccessException | InstantiationException |
				InvocationTargetException | NoSuchMethodException e
		) {
			throw handle(type, unboxedParamTypes, e);
		}
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
		if(exception instanceof NoSuchMethodException) {
			return constructorNotFound(type, types);
		} else {
			return constructionException(type, exception);
		}
	}


	private static <C> C tryInstantiation(
		Class<C> type,
		Class<?>[] paramTypes,
		Class<?>[] unboxedParamTypes,
		Object... parameters
	) throws ReactiveException {
		try {
			if(parameters.length == 0) {
				return type.newInstance();
			}

			C real;
			try {
				real = instanciate(type, paramTypes, parameters);
			} catch(NoSuchMethodException e) {
				//try again with unboxed types
				real = instanciate(type, unboxedParamTypes, parameters);
			}

			return real;
		} catch(Exception e) {
			throw handle(type, unboxedParamTypes, e);
		}
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


	@Override
	public void set(Map<String, Object> changed) {
		thisWrapper.set(changed);
	}


	@Override
	public void set(String property, Object value) throws Exception {
		thisWrapper.set(property, value);
	}

	@Override
	public Map<String, Object> getState() {
		return thisWrapper.getState();
	}
}
