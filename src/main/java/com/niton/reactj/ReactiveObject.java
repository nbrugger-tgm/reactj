package com.niton.reactj;

import com.niton.reactj.annotation.Unreactive;
import com.niton.reactj.exceptions.ReactiveException;
import com.niton.reactj.util.ReactiveReflectorUtil;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.niton.reactj.exceptions.ReactiveException.constructionException;
import static com.niton.reactj.exceptions.ReactiveException.constructorNotFound;

/**
 * The base class to make a Object reactive (usable in ReactiveComponents).
 *
 * The most common way to use this component is by extending it and call {@link ReactiveObject#react()} whenever needed
 */
public class ReactiveObject implements Reactable {
	private final Object store;

	@Unreactive
	protected final List<Observer<?>> listeners = new ArrayList<>();

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
	public static <C> ReactiveProxy<C> createProxy(Class<C> type, Object... constructorParams)
	throws
	ReactiveException {
		Class<?>[] paramTypes = Arrays.stream(constructorParams)
		                              .map(Object::getClass)
		                              .toArray(Class[]::new);

		Class<?>[] unboxedParamTypes = ReactiveReflectorUtil.unboxTypes(paramTypes);

		C real = tryInstantiation(type,
		                          paramTypes,
		                          unboxedParamTypes,
		                          constructorParams);
		ReactiveProxy<C> model = new ReactiveProxy<>(real);
		C wrapped = constructProxy(type,
		                           paramTypes,
		                           unboxedParamTypes,
		                           model,
		                           constructorParams);
		model.setProxy(wrapped);
		return model;
	}

	public static <C extends ProxySubject> C create(Class<C> type, Object... constructorParams)
	throws
	ReactiveException {
		return (C) createProxy((Class<? extends Object>) type, constructorParams).getObject();
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
			} catch (NoSuchMethodException e) {
				wrapped = (C) factory.create(unboxedParamTypes, constructorParams, model);
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
	 * Deal with the error
	 * @param exception the exception to deal with
	 */
	private static <C> ReactiveException handle (
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


	private static <C> C tryInstantiation(
			Class<C> type,
			Class<?>[] paramTypes,
			Class<?>[] unboxedParamTypes,
			Object... parameters
	) {
		try {
			if (parameters.length == 0) {
				return type.newInstance();
			}

			C real;
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
	public void bind(Observer<?> observer) {
		listeners.add(observer);
	}

	@Override
	public Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(store);
	}

	@Override
	public void unbind(Observer<?> observer) {
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
	public void set(String property, Object value) throws Exception {
		ReactiveReflectorUtil.updateField(store, property, value);
	}

	@Override
	public void unbindAll() {
		listeners.clear();
	}
}
