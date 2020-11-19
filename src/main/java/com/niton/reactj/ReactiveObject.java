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

	public static <C> ReactiveProxy<C> create(Class<C> o, Object... constructorParameters) throws ReactiveException {

		C wrapped = null;
		C real = null;
		Class<?>[] paramTypes = Arrays.stream(constructorParameters).map(Object::getClass).toArray(Class[]::new);
		Class<?>[] unboxedParamTypes = Arrays.stream(paramTypes).map(c -> MethodType.methodType(c).unwrap().returnType()).toArray(Class[]::new);
		try {
			Constructor<C> constructor = o.getConstructor(paramTypes);
			constructor.setAccessible(true);
			real = constructor.newInstance(constructorParameters);
		} catch (InstantiationException | InvocationTargetException e) {
			ReactiveException exception = new ReactiveException("Couldn't construct " + o.getSimpleName());
			exception.initCause(e);
			throw exception;
		} catch (IllegalAccessException ignored) {
		} catch (NoSuchMethodException e) {
			try {
				Constructor<C> constructor = o.getConstructor(unboxedParamTypes);
				constructor.setAccessible(true);
				real = constructor.newInstance(constructorParameters);
			} catch (NoSuchMethodException ex) {
				throw new ReactiveException("No constructor(" + Arrays.stream(paramTypes).map(Class::getSimpleName).collect(Collectors.joining(", ")) + ") found in class " + o.getSimpleName());
			} catch (IllegalAccessException ignored) {
			} catch (InstantiationException | InvocationTargetException instantiationException) {
				ReactiveException exception = new ReactiveException("Couldn't construct " + o.getSimpleName());
				exception.initCause(e);
				throw exception;
			}

		}
		ReactiveModel<C> model = new ReactiveModel<>(real);
		ProxyFactory factory = new ProxyFactory();
		factory.setSuperclass(o);
		try {
			wrapped = (C) factory.create(paramTypes, constructorParameters, model);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			try {
				wrapped = (C) factory.create(unboxedParamTypes, constructorParameters, model);
			} catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException instantiationException) {
				ReactiveException ex = new ReactiveException("Error on creating proxy");
				ex.initCause(e);
				throw ex;
			}
		}
		return new ReactiveProxy<C>(wrapped, model);
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

	public void react(String name,Object obj) {
		listeners.forEach(l -> l.modelChanged(Collections.singletonMap(name, obj)));
	}

	@Override
	public void set(String property, Object value) throws Throwable {
		ReactiveReflectorUtil.updateField(this, property, value);
	}
}
