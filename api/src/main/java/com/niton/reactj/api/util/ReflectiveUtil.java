package com.niton.reactj.api.util;

import com.niton.reactj.api.exceptions.ReflectiveCallException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
     *
     * @return the return value of the method call
     */
    public static Object executeCall(Object target, Method method, Object... args)
            throws InvocationTargetException, IllegalAccessException {
        try {
            return target.getClass()
                         .getMethod(method.getName(), method.getParameterTypes())
                         .invoke(target, args);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    incompatibleMethodMessage(method, target, args),
                    e
            );
        }
    }

    private static String incompatibleMethodMessage(
            Method method,
            Object target,
            Object[] args
    ) {
        return format(
                "Method %s is not compatible with %s.%s(%s)",
                getMethodSignature(method),
                target.getClass().getSimpleName(),
                method.getName(),
                Arrays.stream(args)
                      .map(Object::getClass)
                      .map(Class::getTypeName)
                      .collect(Collectors.joining())
        );
    }

    /**
     * Get the method meeting the signature within the given type
     *
     * @param thisMethod the method to search
     * @param type       the class to search in
     *
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

    /**
     * @param val the object to check the type of
     *
     * @return true if val is usable as method parameter with type paramType
     */
    public static boolean isFitting(Object val, Class<?> paramType) {
        Class<?> base = val.getClass();
        Class<?> unwrapped = MethodType.methodType(base)
                                       .unwrap()
                                       .returnType();

        boolean unwrappedValid = paramType.isAssignableFrom(base);
        boolean wrappedValid   = paramType.isAssignableFrom(unwrapped);

        return unwrappedValid || wrappedValid;
    }


    public static Class<?>[] unboxTypes(Class<?>... paramTypes) {
        return Arrays
                .stream(paramTypes)
                .map(c -> MethodType.methodType(c).unwrap().returnType())
                .toArray(Class[]::new);
    }

    public static boolean isMutableInstanceVar(Field field) {
        return !isStatic(field) && !isFinal(field);
    }

    public static boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    public static boolean isFinal(Field field) {
        return Modifier.isFinal(field.getModifiers());
    }


    /**
     * THIS IS PURE EVIL ... but necessary
     *
     * @return true if it the magic worked
     */
    public static boolean setFinal(Field field, Object target, Object newValue)
            throws NoSuchFieldException, IllegalAccessException {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        if (!modifiersField.trySetAccessible())
            return false;
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(target, newValue);
        return false;
    }
}
