package com.niton.reactj.util;

import com.niton.reactj.ReactiveBinder;
import com.niton.reactj.ReactiveComponent;
import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.ReactiveListener;
import com.niton.reactj.exceptions.ReactiveException;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.niton.reactj.annotation.ReactivResolution.ReactiveResolutions.DEEP;
import static com.niton.reactj.util.ReflectiveUtil.invalidMethodParameterException;


public final class ReactiveComponentUtil {
	private ReactiveComponentUtil() {
	}

	/**
	 * Registers all @{@link com.niton.reactj.annotation.ReactiveListener} annotated methods in component to the binder
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

		for(Method method : methods) {
			processAnnotatedMethod(component, binder, method);
		}
	}

	/**
	 * Attaches an annotated method to the reactive binder (uno-direction)
	 * @param component the component instance the method originates from
	 * @param binder the binder to bind the method to
	 * @param method the method to bind
	 */
	private static void processAnnotatedMethod(ReactiveComponent<?> component,
	                                           ReactiveBinder<?> binder,
	                                           Method method) {
		if(method.getParameterTypes().length > 1) {
			throw new ReactiveException(
				String.format("@ReactiveListener method '%s' has more than one parameter", method)
			);
		}

		String mapTarget = method.getAnnotation(ReactiveListener.class).value();
		binder.bind(mapTarget, (val) -> dynamicCall(component, method, val));
	}

	private static void dynamicCall(ReactiveComponent<?> component, Method method, Object val) {
		try {
			if(!method.isAccessible())
				method.setAccessible(true);
			if(method.getParameterTypes().length == 1) {
				Class<?> paramType = method.getParameterTypes()[0];
				if(!ReactiveReflectorUtil.isFitting(val, paramType)) {
					throw invalidMethodParameterException(method, val);
				}
				method.invoke(component, val);
			} else {
				method.invoke(component);
			}
		} catch(IllegalAccessException | InvocationTargetException e) {
			throw new ReactiveException(
				String.format("Failed to call automatic binding (%s): %s", method, e)
			);
		}
	}


}
