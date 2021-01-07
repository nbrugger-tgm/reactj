package com.niton.reactj;

import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.exceptions.ReactiveException;
import com.niton.reactj.mvc.ReactiveBinder;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.niton.reactj.annotation.ReactivResolution.ReactiveResolutions.DEEP;

public interface ReactiveComponent<C> {
	void createBindings(ReactiveBinder binder);

	void registerListeners(C controller);

	default void createAnnotatedBindings(ReactiveBinder binder) {
		Class<? extends ReactiveComponent> viewClass = this.getClass();
		Method[] methods = MethodUtils.getMethodsWithAnnotation(
				viewClass,
				Reactive.class,
				viewClass.isAnnotationPresent(ReactivResolution.class)
						&&
						viewClass.getAnnotation(ReactivResolution.class).value() == DEEP,
				true);
		for (Method method : methods) {
			processAnnotatedMethod(binder, method);
		}
	}

	default void processAnnotatedMethod(ReactiveBinder binder, Method method) {
		if (method.getParameterTypes().length > 1) {
			throw new ReactiveException(
					String.format("@Reactive method %s has more than one parameter", method)
			);
		}

		String mapTarget = method.getAnnotation(Reactive.class).value();
		binder.bind(mapTarget, (val) -> dynamicCall(method, val));
	}

	default void dynamicCall(Method method, Object val) {
		try {
			if (method.getParameterTypes().length == 1) {
				Class<?> paramType = method.getParameterTypes()[0];
				if (!ReactiveReflectorUtil.isFitting(val, paramType)) {
					throw invalidMethodParameterException(method, val);
				}
				method.setAccessible(true);
				method.invoke(this, val);
			} else {
				method.setAccessible(true);
				method.invoke(this);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new ReactiveException(
					String.format("Failed to call automatic binding (%s): %s",method,e)
			);
		}
	}

	default ClassCastException invalidMethodParameterException(Method method, Object val) {
		String message = String.format("Method %s doesnt accepts type %s",
		                               getMethodSignature(method),
		                               val.getClass().getTypeName());
		return new ClassCastException(message);
	}


	default String getMethodSignature(Method method) {
		return String.format("%s.%s(%s)",
		                     method.getDeclaringClass().getSimpleName(),
		                     method.getName(),
		                     getMethodParamSignature(method));
	}

	default String getMethodParamSignature(Method method) {
		return Arrays
				.stream(method.getParameterTypes())
				.map(Class::getTypeName)
				.collect(Collectors.joining(", "));
	}
}
