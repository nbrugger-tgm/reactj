package com.niton.reactj.objects;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.exceptions.ReactiveAccessException;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.mvc.ReactiveComponent;
import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.objects.annotations.ReactiveListener;
import com.niton.reactj.objects.dsl.ObjectDsl;
import com.niton.reactj.objects.observer.ObjectObserver;
import com.niton.reactj.objects.observer.PropertyObservation;
import com.niton.reactj.objects.proxy.ReactiveProxy;
import com.niton.reactj.objects.reflect.Reflective;
import com.niton.reactj.objects.util.ReactiveReflectorUtil;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import static com.niton.reactj.api.util.ReflectiveUtil.*;
import static java.lang.String.format;

public abstract class ReactiveObjectComponent<M extends Reactable & Reflective, V>
		extends ReactiveComponent<M, PropertyObservation<M>, V> {
	private final EventEmitter<M> onModelChange = new EventEmitter<>();

	protected ReactiveObjectComponent() {
		super(new ObjectObserver<>());
	}

	@Override
	protected void registerBindings(
			EventEmitter<PropertyObservation<M>> observerEvent
	) {
		observerEvent.listen(obs -> onModelChange.fire(obs.observed));

		ObjectDsl<M> binder = ObjectDsl.create(this::getModel, onModelChange);

		createBindings(binder, onModelChange, observerEvent);
		registerAnnotatedBindings(binder, observerEvent);
	}

	protected abstract void createBindings(
			ObjectDsl<M> builder,
			EventEmitter<M> onModelChange,
			EventEmitter<PropertyObservation<M>> onPropertyChange
	);

	/**
	 * Registers all @{@link ReactiveListener} annotated methods in
	 * component to the
	 * binder
	 */
	private void registerAnnotatedBindings(
			ObjectDsl<M> builder,
			EventEmitter<PropertyObservation<M>> observerEvent
	) {
		for (Method listenerMethod : getListenerMethods()) {
			processAnnotatedMethod(listenerMethod, builder, observerEvent);
		}

	}

	public Method[] getListenerMethods() {
		return MethodUtils.getMethodsWithAnnotation(
				getClass(),
				ReactiveListener.class,
				ReactiveReflectorUtil.goDeep(getClass()),
				true
		);
	}

	/**
	 * Attaches an annotated method to the reactive binder (uno-direction)
	 *
	 * @param binder the binder to bind the method to
	 * @param method the method to bind
	 */
	private void processAnnotatedMethod(
			Method method,
			ObjectDsl<M> binder,
			EventEmitter<PropertyObservation<M>> onChange
	) {
		if (method.getParameterTypes().length > 1) {
			throw parameterCountException(method);
		}

		if (!method.canAccess(this))
			method.setAccessible(true);


		String propertyName = method.getAnnotation(ReactiveListener.class).value();
		binder.call(value -> invokeReactiveListener(method, value))
		      .with(this::propertyValue)
		      .from(onChange)
		      .when(propertyNameIs(propertyName));
	}

	private static ReactiveException parameterCountException(Method method) {
		return new ReactiveException(format(
				"@ReactiveListener method '%s' has more than one parameter",
				method
		));
	}

	private void invokeReactiveListener(Method listener, Object param) {
		try {
			if (listener.getParameterTypes().length == 1) {
				checkParameterType(listener, param);
				listener.invoke(this, param);
			} else if (listener.getParameterTypes().length == 0) {
				listener.invoke(this);
			} else {
				throw parameterCountException(listener);
			}
		} catch (IllegalAccessException eac) {
			throw new ReactiveAccessException(eac);
		} catch (InvocationTargetException e) {
			throw new ReactiveException(
					format("Failed to call automatic binding (%s)", listener),
					e
			);
		}
	}

	protected Object propertyValue(PropertyObservation<M> change) {
		return change.propertyValue;
	}

	protected Predicate<PropertyObservation<M>> propertyNameIs(String name) {
		return observation -> observation.propertyName.equals(name);
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

	protected EventEmitter<M> proxyUnwrap(EventEmitter<ReactiveProxy<M>> onModelChange) {
		EventEmitter<M> unwrappedEmitter = new EventEmitter<>();
		onModelChange.listen(e -> unwrappedEmitter.fire(e.getObject()));
		return unwrappedEmitter;
	}

	protected Predicate<PropertyObservation<M>> propertyValueIs(Predicate<Object> predicate) {
		return observation -> predicate.test(observation.propertyValue);
	}

}
