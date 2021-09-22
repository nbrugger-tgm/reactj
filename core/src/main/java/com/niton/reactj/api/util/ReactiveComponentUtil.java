package com.niton.reactj.api.util;

import com.niton.reactj.api.annotation.ReactivResolution;
import com.niton.reactj.api.annotation.ReactiveListener;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.react.ReactiveBinder;
import com.niton.reactj.api.react.ReactiveComponent;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.niton.reactj.api.annotation.ReactivResolution.ReactiveResolutions.DEEP;
import static com.niton.reactj.api.util.ReflectiveUtil.invalidMethodParameterException;
import static java.lang.String.format;


public final class ReactiveComponentUtil {
	private ReactiveComponentUtil() {
	}

	/**
	 * Registers all @{@link com.niton.reactj.api.annotation.ReactiveListener} annotated methods in component to the binder
	 *
	 * @param binder the binder to register the bindings to
	 */
	public static void createAnnotatedBindings(ReactiveComponent<?> component, ReactiveBinder<?> binder) {
		Class<?> viewClass = component.getClass();

		ReactivResolution resolution = viewClass.getAnnotation(ReactivResolution.class);
		boolean searchSuperClasses = resolution != null && resolution.value() == DEEP;
		Method[] methods = MethodUtils.getMethodsWithAnnotation(
				viewClass,
				ReactiveListener.class,
				searchSuperClasses,
				true
		);

		for (Method method : methods) {
			processAnnotatedMethod(component, binder, method);
		}
	}

	/**
	 * Attaches an annotated method to the reactive binder (uno-direction)
	 *
	 * @param component the component instance the method originates from
	 * @param binder    the binder to bind the method to
	 * @param method    the method to bind
	 */
	private static void processAnnotatedMethod(
			ReactiveComponent<?> component,
			ReactiveBinder<?> binder,
			Method method
	) {
		if (method.getParameterTypes().length > 1) {
			throw new ReactiveException(
					format("@ReactiveListener method '%s' has more than one parameter", method)
			);
		}

		String mapTarget = method.getAnnotation(ReactiveListener.class).value();
		binder.bind(mapTarget, val -> dynamicCall(component, method, val));
	}

	private static void dynamicCall(ReactiveComponent<?> component, Method method, Object val) {
		try {
			if (!method.canAccess(component))
				method.setAccessible(true);

			if (method.getParameterTypes().length == 1) {
				checkParameterType(method, val);
				method.invoke(component, val);
			} else {
				method.invoke(component);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new ReactiveException(format("Failed to call automatic binding (%s)", method), e);
		}
	}

	/**
	 * Checks if the object can be used as parameter for the given method.
	 * It is assumed that the given method has <b>exactly</b> one argument
	 *
	 * @param method the method to fit the object into
	 * @param val    the value to use as parameter
	 */
	private static void checkParameterType(Method method, Object val) {
		Class<?> paramType = method.getParameterTypes()[0];
		if (!ReactiveReflectorUtil.isFitting(val, paramType)) {
			throw invalidMethodParameterException(method, val);
		}
	}


}
