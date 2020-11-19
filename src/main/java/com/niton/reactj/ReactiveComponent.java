package com.niton.reactj;

import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.exceptions.ReactiveException;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public interface ReactiveComponent<C> {
	void createBindings(ReactiveBinder binder);

	void registerListeners(C controller);

	default void createAnnotatedBindings(ReactiveBinder binder) {
		Class<? extends ReactiveComponent> viewClass = this.getClass();
		Method[] methods = MethodUtils.getMethodsWithAnnotation(viewClass, Reactive.class, viewClass.isAnnotationPresent(ReactivResolution.class) && viewClass.getAnnotation(ReactivResolution.class).value() == ReactivResolution.ReactiveResolutions.DEEP, true);
		for (Method method : methods) {
			if (method.getParameterTypes().length > 1) {
				throw new ReactiveException("@Reactive method " + method + " has more than one parameter");
			}
			String mapTarget = method.getAnnotation(Reactive.class).value();
			binder.bind(mapTarget, (val) -> {

				if (method.getParameterTypes().length == 1) {
					if (!val.getClass().isAssignableFrom(method.getParameterTypes()[0]) && !MethodType.methodType(val.getClass()).unwrap().returnType().isAssignableFrom(method.getParameterTypes()[0]))
						throw new ClassCastException("Method " + (method.getDeclaringClass().getSimpleName() + "." + method.getName() + "(" + Arrays.stream(method.getParameterTypes()).map(Class::getTypeName).collect(Collectors.joining(", ")) + ")") + " doesnt accepts type " + val.getClass().getTypeName());
					else
						try {
							method.invoke(this, val);
						} catch (IllegalAccessException | InvocationTargetException e) {
							throw new ReactiveException("Failed to call automatic binding : " + e);
						}
				} else {
					try {
						method.invoke(this);
					} catch (IllegalAccessException | InvocationTargetException e) {
						throw new ReactiveException("Failed to call automatic binding : " + e);
					}
				}
			});
		}

	}
}
