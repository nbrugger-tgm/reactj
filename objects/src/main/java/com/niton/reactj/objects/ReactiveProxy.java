package com.niton.reactj.objects;

import com.niton.reactj.api.observer.Reactable;
import com.niton.reactj.api.react.ReactiveForwarder;
import com.niton.reactj.core.observer.Reflective;
import com.niton.reactj.core.observer.ReflectiveForwarder;

/**
 * A Wrapper for a Proxy.
 *
 * <p>
 * Should only be instantiated by {@link ProxyCreator#create(Object)}
 * </p>
 *
 * @param <T>
 */
public class ReactiveProxy<T> implements ReactiveForwarder, ReflectiveForwarder {
	private final T proxy;

	public ReactiveProxy(T proxy) {
		if (!(proxy instanceof Reactable))
			throw new IllegalArgumentException("A proxy needs to be reactable!");
		this.proxy = proxy;
	}

	@Override
	public Reactable getReactableTarget() {
		return (Reactable) proxy;
	}

	@Override
	public Reflective getReflectiveTarget() {
		return (Reflective) proxy;
	}

	public T getObject() {
		return proxy;
	}
}
