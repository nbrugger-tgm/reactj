package com.niton.reactj.util;

import com.niton.reactj.ReactiveBinder;
import com.niton.reactj.ReactiveComponent;
import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.exceptions.ReactiveException;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.niton.reactj.annotation.ReactivResolution.ReactiveResolutions.DEEP;

public class ReactiveComponentUtil {
	/**
	 * Registers all @Reactive annotated methods in component to the binder
	 *
	 * @param binder the binder to register the bindings to
	 */
	public static void createAnnotatedBindings(ReactiveComponent component, ReactiveBinder binder) {
		Class<? extends ReactiveComponent> viewClass = component.getClass();
		Method[] methods = MethodUtils.getMethodsWithAnnotation(
				viewClass,
				Reactive.class,
				viewClass.isAnnotationPresent(ReactivResolution.class)
						&&
						viewClass.getAnnotation(ReactivResolution.class).value() == DEEP,
				true);
		for (Method method : methods) {
			processAnnotatedMethod(component, binder, method);
		}
	}

	private static void processAnnotatedMethod(ReactiveComponent component,
	                                           ReactiveBinder binder,
	                                           Method method) {
		if (method.getParameterTypes().length > 1) {
			throw new ReactiveException(
					String.format("@Reactive method %s has more than one parameter", method)
			);
		}

		String mapTarget = method.getAnnotation(Reactive.class).value();
		binder.bind(mapTarget, (val) -> dynamicCall(component, method, val));
	}

	private static void dynamicCall(ReactiveComponent component, Method method, Object val) {
		try {
			method.setAccessible(true);
			if (method.getParameterTypes().length == 1) {
				Class<?> paramType = method.getParameterTypes()[0];
				if (!ReactiveReflectorUtil.isFitting(val, paramType)) {
					throw invalidMethodParameterException(method, val);
				}
				method.invoke(component, val);
			} else {
				method.invoke(component);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new ReactiveException(
					String.format("Failed to call automatic binding (%s): %s", method, e)
			);
		}
	}

	private static ClassCastException invalidMethodParameterException(Method method, Object val) {
		String message = String.format("Method %s doesnt accepts type %s",
		                               getMethodSignature(method),
		                               val.getClass().getTypeName());
		return new ClassCastException(message);
	}


	private static String getMethodSignature(Method method) {
		return String.format("%s.%s(%s)",
		                     method.getDeclaringClass().getSimpleName(),
		                     method.getName(),
		                     getMethodParamSignature(method));
	}

	private static String getMethodParamSignature(Method method) {
		return Arrays
				.stream(method.getParameterTypes())
				.map(Class::getTypeName)
				.collect(Collectors.joining(", "));
	}
}
