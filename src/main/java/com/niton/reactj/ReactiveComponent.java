package com.niton.reactj;

import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.Reactive;
import org.apache.commons.lang3.reflect.MethodUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventObject;

public interface ReactiveComponent<C>  {
	void createBindings(ReactiveBinder controller);
	void registerListeners(C controller);

	default void createAnnotatedBindings(ReactiveBinder binder){
		Class<? extends ReactiveComponent> viewClass = this.getClass();
		Method[] methods = MethodUtils.getMethodsWithAnnotation(viewClass, Reactive.class,viewClass.isAnnotationPresent(ReactivResolution.class) && viewClass.getAnnotation(ReactivResolution.class).value() == ReactivResolution.ReactiveResolutions.DEEP,true);
		for (Method method : methods) {
			String mapTarget = method.getAnnotation(Reactive.class).value();
			binder.bind(mapTarget,(val)-> {
				try {
					MethodUtils.invokeMethod(this,true,method.getName(),val);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
				}
			});
		}

	}
}
