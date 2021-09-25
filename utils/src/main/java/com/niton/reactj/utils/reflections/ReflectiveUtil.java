package com.niton.reactj.utils.reflections;

import com.niton.reactj.utils.exceptions.ReflectiveCallException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

public final class ReflectiveUtil {
	private ReflectiveUtil() {
	}

	public static ClassCastException invalidMethodParameterException(Method method, Object val) {
		String message = format(
				"Method %s doesn't accept type %s",
				getMethodSignature(method),
				val.getClass().getTypeName()
		);
		return new ClassCastException(message);
	}

	public static String getMethodSignature(Method method) {
		return format(
				"%s.%s(%s)",
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
			return target.getClass().getMethod(
							method.getName(),
							method.getParameterTypes()
					)
					.invoke(target, args);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
					format(
							"Method %s is not compatible with %s.%s(%s)",
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

	/**
	 * Get the method meeting the signature within the given type
	 *
	 * @param thisMethod the method to search
	 * @param type       the class to search in
	 * @return the method that will return `type` as {@link Method#getDeclaringClass()}
	 */
	public static Method getOriginMethod(Method thisMethod, Class<?> type) {
		try {
			return type.getDeclaredMethod(thisMethod.getName(), thisMethod.getParameterTypes());
		} catch (NoSuchMethodException e) {
			throw new ReflectiveCallException(format(
					"There is no method in class '%s' that matches : %s",
					type.getSimpleName(),
					getMethodSignature(thisMethod)
			));
		}
	}
}
