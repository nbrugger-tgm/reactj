package com.niton.reactj.api.util;

import com.niton.reactj.api.annotation.Reactive;
import com.niton.reactj.api.annotation.Unreactive;
import com.niton.reactj.api.exceptions.ReactiveException;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.reflect.Modifier.isStatic;

/**
 * This class is intended for internal use only
 * <p>
 * Serves several methods for reflective access specifically for @Annotations
 */
public final class ReactiveReflectorUtil {
	private static final Map<String, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();

	private ReactiveReflectorUtil() {
	}

	/**
	 * @param val the object to check the type of
	 * @return true if val is usable as method parameter with type paramType
	 */
	public static boolean isFitting(Object val, Class<?> paramType) {
		Class<?> base = val.getClass();
		Class<?> unwrapped = MethodType.methodType(base)
		                               .unwrap()
		                               .returnType();

		boolean unwrappedValid = base.isAssignableFrom(paramType);
		boolean wrappedValid   = unwrapped.isAssignableFrom(paramType);

		return unwrappedValid || wrappedValid;
	}

	/**
	 * Returns the object as Map, by applying @Reactive and @Unreactive annotations
	 *
	 * @param model the object to convert
	 * @return the map containing all (renamed) properties
	 */
	public static Map<String, Object> getState(Object model) {
		HashMap<String, Object> state    = new HashMap<>();
		Class<?>                type     = model.getClass();
		String                  typeName = type.getName();

		Field[] fields = FIELD_CACHE.computeIfAbsent(typeName, n -> loadRelevantFields(type));

		try {
			readState(model, fields, state);
		} catch (IllegalAccessException e) {
			throw new ReactiveException("Cannot read field of model", e);
		}
		return state;
	}

	/**
	 * Resolves all fields depending on @ReactiveResolution
	 *
	 * @param type the type to scan
	 * @return the fields as array
	 */
	public static Field[] loadRelevantFields(Class<?> type) {
		if (ReflectiveUtil.goDeep(type))
			return FieldUtils.getAllFields(type);
		else
			return type.getDeclaredFields();
	}


	public static void readState(Object model, Field[] fields, Map<String, Object> state)
			throws IllegalAccessException {
		for (Field f : fields) {
			if (isStatic(f.getModifiers()) || f.isAnnotationPresent(Unreactive.class)) {
				continue;
			}
			f.setAccessible(true);
			state.put(getReactiveName(f), f.get(model));
		}
	}

	/**
	 * Resolves the name by @Reactive
	 *
	 * @param field the field to get the name from
	 * @return the name to be used for this field
	 */
	public static String getReactiveName(Field field) {
		return field.isAnnotationPresent(Reactive.class) ?
		       field.getAnnotation(Reactive.class).value() :
		       field.getName();
	}

	/**
	 * Update the field of an object without triggering react()
	 *
	 * @param model    the object to update the field
	 * @param property the name of the field to update (regarding to @Reactive)
	 * @param value    the value to set the property to
	 */
	public static void updateField(Object model, String property, Object value) {
		Class<?> type      = model.getClass();
		Field[]  fields    = getFields(type);
		Field    propField = findField(property, fields);
		try {
			FieldUtils.writeField(propField, model, value, true);
		} catch (IllegalAccessException e) {
			throw new ReactiveException("Updating model failed", e);
		}
	}

	private static Field[] getFields(Class<?> type) {
		String typeName = type.getName();
		return FIELD_CACHE.computeIfAbsent(typeName, n -> loadRelevantFields(type));
	}

	private static Field findField(String property, Field[] fields) {
		Field propField = null;
		for (Field f : fields) {
			if (getReactiveName(f).equals(property)) {
				propField = f;
				break;
			}
		}
		if (propField == null) {
			throw new IllegalArgumentException("no field with name \"" + property + "\" found");
		}
		return propField;
	}

	public static Class<?>[] unboxTypes(Class<?>... paramTypes) {
		return Arrays
				.stream(paramTypes)
				.map(c -> MethodType.methodType(c).unwrap().returnType())
				.toArray(Class[]::new);
	}
}
