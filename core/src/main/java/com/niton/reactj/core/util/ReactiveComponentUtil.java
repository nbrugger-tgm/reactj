package com.niton.reactj.core.util;

import com.niton.reactj.core.annotation.ReactiveListener;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Method;

import static com.niton.reactj.utils.reflections.ReflectiveUtil.*;


public final class ReactiveComponentUtil {
	private ReactiveComponentUtil() {
	}

	public static Method[] getListenerMethods(Class<?> viewClass) {
		return MethodUtils.getMethodsWithAnnotation(
				viewClass,
				ReactiveListener.class,
				ReactiveReflectorUtil.goDeep(viewClass),
				true
		);
	}


	/**
	 * Checks if the object can be used as parameter for the given method.
	 * It is assumed that the given method has <b>exactly</b> one argument
	 *
	 * @param method the method to fit the object into
	 * @param val    the value to use as parameter
	 */
	public static void checkParameterType(Method method, Object val) {
		Class<?> paramType = method.getParameterTypes()[0];
		if (!isFitting(val, paramType)) {
			throw invalidMethodParameterException(method, val);
		}
	}


}
