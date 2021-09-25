package com.niton.reactj.core.react;

import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.react.ReactiveForwarder;

/**
 * A Wrapper for a Proxy.
 *
 * <p>
 * Should only be instantiated by {@link com.niton.reactj.core.proxy.ProxyCreator#create(Object)}
 * </p>
 *
 * @param <T>
 */
public class ReactiveProxy<T> implements ReactiveForwarder {
	private final T proxy;

	public ReactiveProxy(T proxy) {
		if (!(proxy instanceof Reactable))
			throw new IllegalArgumentException("A proxy needs to be reactable!");
		this.proxy = proxy;
	}

	public T getObject() {
		return proxy;
	}

	@Override
	public Reactable getReactableTarget() {
		return (Reactable) proxy;
	}
}
