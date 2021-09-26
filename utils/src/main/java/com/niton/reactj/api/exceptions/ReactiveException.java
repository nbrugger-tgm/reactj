package com.niton.reactj.api.exceptions;


import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The general exception that is thrown when a known error occurs
 */
public class ReactiveException extends RuntimeException {

	public ReactiveException(String message) {
		super(message);
	}

	public ReactiveException(String message, Throwable fail) {
		super(message, fail);
	}

	public static ReactiveException bindingException(
			String key,
			Object value,
			Object converted,
			ClassCastException castException
	) {
		Class<?> original      = value.getClass();
		Class<?> convertedType = converted.getClass();

		ReactiveException exception;
		if (convertedType.equals(original)) {
			exception = badBindingTarget(key, original);
		} else {
			exception = badConverterBindingTarget(key, original, convertedType);
		}
		exception.initCause(castException);
		return exception;
	}

	private static ReactiveException badBindingTarget(String key, Class<?> original) {
		return new ReactiveException(String.format(
				"Bad binding for \"%s\". Target function doesn't accept type %s",
				key,
				original.getTypeName()
		));
	}

	private static ReactiveException badConverterBindingTarget(
			String key,
			Class<?> original,
			Class<?> convertedType
	) {
		return new ReactiveException(String.format(
				"Bad binding for \"%s\". Target function doesn't accept converted (from %s to %s)",
				key,
				original.getTypeName(),
				convertedType.getTypeName()
		));
	}

	public static ReactiveException badConverterException(String property, Class<?> type) {
		return new ReactiveException(String.format(
				"Bad converter. A converter for \"%s\" doesn't accept type %s",
				property,
				type.getSimpleName()
		));
	}

	public static <C> ReactiveException constructorNotFound(Class<C> type, Class<?>... paramTypes) {
		return new ReactiveException(
				String.format(
						"No constructor(%s) found in class %s",
						Arrays.stream(paramTypes)
						      .map(Class::getSimpleName)
						      .collect(Collectors.joining(", ")),
						type.getSimpleName()
				));
	}

	public static <C> ReactiveException constructionException(
			Class<C> type,
			Exception cause
	) {
		return new ReactiveException(
				String.format("Couldn't construct %s", type.getSimpleName()),
				cause
		);
	}
}
