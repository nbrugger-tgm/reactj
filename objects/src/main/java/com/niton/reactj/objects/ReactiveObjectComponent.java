package com.niton.reactj.objects;

import com.niton.reactj.api.binding.builder.ReactiveBinder;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.exceptions.ReactiveAccessException;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.mvc.ReactiveComponent;
import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.objects.reflect.Reflective;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

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
		createBindings(builder, onModelChange, observerEvent);
		registerAnnotatedBindings(builder, observerEvent);
	}

	protected abstract void createBindings(
			ReactiveBinder<ModelCallBuilder<M>> builder,
			EventEmitter<M> onModelChange,
			EventEmitter<PropertyObservation<M>> onPropertyChange
	);

	/**
	 * Registers all @{@link com.niton.reactj.core.annotation.ReactiveListener} annotated methods in
	 * component to the
	 * binder
	 */
	private void registerAnnotatedBindings(
			ReactiveBinder<ModelCallBuilder<M>> builder,
			EventEmitter<PropertyObservation<M>> observerEvent
	) {
		for (Method listenerMethod : ReactiveComponentUtil.getListenerMethods(getClass())) {
			processAnnotatedMethod(listenerMethod, builder, observerEvent);
		}

	}

	/**
	 * Attaches an annotated method to the reactive binder (uno-direction)
	 *
	 * @param binder the binder to bind the method to
	 * @param method the method to bind
	 */
	private void processAnnotatedMethod(
			Method method,
			ReactiveBinder<ModelCallBuilder<M>> binder,
			EventEmitter<PropertyObservation<M>> onChange
	) {
		if (method.getParameterTypes().length > 1) {
			throw parameterCountException(method);
		}

		if (!method.canAccess(this))
			method.setAccessible(true);


		String propertyName = method.getAnnotation(ReactiveListener.class).value();
		binder.newBinding()
		      .call(value -> invokeReactiveListener(method, value))
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
				ReactiveComponentUtil.checkParameterType(listener, param);
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

	private EventEmitter<M> proxyUnwrap(EventEmitter<ReactiveProxy<M>> onModelChange) {
		EventEmitter<M> unwrappedEmitter = new EventEmitter<>();
		onModelChange.listen(e -> unwrappedEmitter.fire(e.getObject()));
		return unwrappedEmitter;
	}

	protected Predicate<PropertyObservation<M>> propertyValueIs(Predicate<Object> predicate) {
		return observation -> predicate.test(observation.propertyValue);
	}
}
