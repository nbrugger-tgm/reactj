package com.niton.reactj.api.util;

import com.niton.reactj.api.exceptions.ReactiveException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

public final class ReflectiveUtil {
	private ReflectiveUtil() {
	}

	public static ClassCastException invalidMethodParameterException(Method method, Object val) {
		String message = format("Method %s doesn't accept type %s",
				getMethodSignature(method),
				val.getClass().getTypeName()
		);
		return new ClassCastException(message);
	}


	public static String getMethodSignature(Method method) {
		return format("%s.%s(%s)",
				method.getDeclaringClass().getSimpleName(),
				method.getName(),
				getMethodParamSignature(method)
		);
	}

	public static String getMethodParamSignature(Method method) {
		return Arrays
				.stream(method.getParameterTypes())
				.map(Class::getTypeName)
				.collect(Collectors.joining(", "));
	}

	/**
	 * Call {@code target.method(args)}
	 *
	 * @param target the object to execute on
	 * @param method the method to execute
	 * @param args   the arguments to pass
	 * @return the return value of the method call
	 */
	public static Object executeCall(Object target, Method method, Object[] args)
			throws InvocationTargetException, IllegalAccessException {
		try {
			return target.getClass().getMethod(method.getName(),
							method.getParameterTypes()
					)
					.invoke(target, args);
		} catch (NoSuchMethodException e) {
			throw new ReactiveException(
					format("Method %s is not compatible with %s.%s(%s)",
							getMethodSignature(method),
							target.getClass().getSimpleName(),
							method.getName(),
							Arrays.stream(args)
									.map(Object::getClass)
									.map(Class::getTypeName)
									.collect(Collectors.joining())
					), e);
		}
	}

	public static Method getOriginMethod(Method thisMethod, Class<?> type) {
		try {
			return type.getDeclaredMethod(thisMethod.getName(), thisMethod.getParameterTypes());
		} catch (NoSuchMethodException e) {
			throw new ReactiveException(format("There is no method in class '%s' that matches : %s",
					type.getSimpleName(),
					getMethodSignature(thisMethod)
			));
		}
	}
}
