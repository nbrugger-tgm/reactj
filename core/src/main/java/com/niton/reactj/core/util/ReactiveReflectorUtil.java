package com.niton.reactj.core.util;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.core.annotation.Reactive;
import com.niton.reactj.core.annotation.ReactiveResolution;
import com.niton.reactj.core.annotation.ReactiveResolution.ReactiveResolutionType;
import com.niton.reactj.core.annotation.Unreactive;
import com.niton.reactj.utils.reflections.ReflectiveUtil;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.niton.reactj.core.annotation.ReactiveResolution.ReactiveResolutionType.DEEP;
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
	 * Returns the object as Map, by applying @Reactive and @Unreactive annotations
	 *
	 * @param model the object to convert
	 *
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
	 *
	 * @return the fields as array
	 */
	public static Field[] loadRelevantFields(Class<?> type) {
		if (goDeep(type))
			return Arrays.stream(FieldUtils.getAllFields(type))
			             .filter(f -> !ReflectiveUtil.isFinal(f))
			             .toArray(Field[]::new);
		else
			return Arrays.stream(type.getDeclaredFields())
			             .filter(f -> !ReflectiveUtil.isFinal(f))
			             .toArray(Field[]::new);
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
	 * Checks if the class should be scanned deeply
	 *
	 * @param type the type to check
	 *
	 * @return true if the class is annotated with {@link ReactiveResolution}({@link ReactiveResolutionType#FLAT})
	 */
	public static boolean goDeep(Class<?> type) {
		return !type.isAnnotationPresent(ReactiveResolution.class) ||
				type.getAnnotation(ReactiveResolution.class).value() == DEEP;
	}

	/**
	 * Resolves the name by @Reactive
	 *
	 * @param field the field to get the name from
	 *
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
		return FIELD_CACHE.computeIfAbsent(typeName, n -> ReactiveReflectorUtil.loadRelevantFields(type));
	}

	private static Field findField(String property, Field[] fields) {
		Field propField = null;
		for (Field f : fields) {
			if (ReactiveReflectorUtil.getReactiveName(f).equals(property)) {
				propField = f;
				break;
			}
		}
		if (propField == null) {
			throw new IllegalArgumentException("no field with name \"" + property + "\" found");
		}
		return propField;
	}

}
