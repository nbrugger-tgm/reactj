package com.niton.reactj.objects.proxy;

import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.react.ReactiveForwarder;
import com.niton.reactj.objects.reflect.Reflective;
import com.niton.reactj.objects.reflect.ReflectiveForwarder;

/**
 * A Wrapper for a Proxy.
 *
 * <p>
 * Should only be instantiated by {@link ProxyCreator#create(Object)}
 * </p>
 *
 * @param <T> the proxied type
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
