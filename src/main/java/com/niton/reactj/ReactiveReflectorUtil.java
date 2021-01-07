package com.niton.reactj;

import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.annotation.Unreactive;
import com.niton.reactj.exceptions.ReactiveException;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ReactiveReflectorUtil {
	private static final Map<String, Field[]> fieldCache = new HashMap<>();

	public static boolean isFitting(Object val, Class<?> paramType) {
		Class<?> base = val.getClass();
		Class<?> unwrapped = MethodType.methodType(base)
		                               .unwrap()
		                               .returnType();
		/*
		 * True if val.getClass() is usable as first Parameter
		 */
		boolean unwrappedValid = base.isAssignableFrom(paramType);
		/*
		 * True if the unwrapped class fits into the parameter type
		 */
		boolean wrappedValid = unwrapped.isAssignableFrom(paramType);
		return !unwrappedValid && !wrappedValid;
	}

	public static Map<String, Object> getState(Object model) {
		HashMap<String, Object> state    = new HashMap<>();
		Class<?>                type     = model.getClass();
		String                  typeName = type.getName();
		Field[]                 fields   = fieldCache.get(typeName);
		if (fields == null) {
			fieldCache.put(typeName, fields = loadRelevantFields(type));
		}
		try {
			ReactiveReflectorUtil.readState(model, fields, state);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return state;
	}

	public static Field[] loadRelevantFields(Class<?> type) {
		Field[] fields;
		if (type.isAnnotationPresent(ReactivResolution.class) && type.getDeclaredAnnotation(
				ReactivResolution.class).value() == ReactivResolution.ReactiveResolutions.FLAT) {
			fields = type.getDeclaredFields();
		} else {
			fields = FieldUtils.getAllFields(type);
		}
		return fields;
	}

	public static void readState(Object model, Field[] fields, Map<String, Object> state)
	throws IllegalAccessException {
		for (Field f : fields) {
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			if (f.isAnnotationPresent(Unreactive.class)) {
				continue;
			}
			state.put(getReactiveName(f), FieldUtils.readField(f, model, true));
		}
	}

	public static String getReactiveName(Field f) {
		return f.isAnnotationPresent(Reactive.class) ? f.getAnnotation(Reactive.class)
		                                                .value() : f.getName();
	}

	public static void updateField(Object model, String property, Object value) throws Throwable {
		Class<?> type     = model.getClass();
		String   typeName = type.getName();
		Field[]  fields   = fieldCache.get(typeName);
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (getReactiveName(f).equals(property)) {
				try {
					FieldUtils.writeField(f, model, value, true);
				} catch (IllegalAccessException e) {
					throw new ReactiveException("Updating model failed").initCause(e);
				}
				return;
			}
		}
	}
}
