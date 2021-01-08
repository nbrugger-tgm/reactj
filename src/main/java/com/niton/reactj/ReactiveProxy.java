package com.niton.reactj;

import com.niton.reactj.util.ReactiveReflectorUtil;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.niton.reactj.ReactiveStrategy.REACT_ON_SETTER;

/**
 * A proxy providing automatic reacting to method calls.
 *
 * This is used to make objects reactive that are not reactive by default and therefore be able
 * to use them in ReactiveComponents
 *
 * @param <M> The type this Model is going to wrap
 */
public class ReactiveProxy<M> implements MethodHandler, Reactable {
	protected final List<Observer<?>> listeners = new ArrayList<>();
	private final    M                 backend;
	private     M                      proxy;
	private         ReactiveStrategy  strategy  = REACT_ON_SETTER;
	private         String[]          reactTo;

	/**
	 * This returns the mutable Objects.
	 *
	 * <br/><b>Calls to this object will be reacted to!</b>
	 * @return the reactive object
	 */
	public M getObject() {
		return proxy;
	}
	void setProxy(M proxy){
		this.proxy = proxy;
	}

	/**
	 * Creates a proxy forwarding Reactive calls to 'real'
	 *
	 * @param real the real model behind. will be used as storage and not be directly accessible anymore
	 */
	public ReactiveProxy(M real) {
		this.backend = real;
	}

	public ReactiveStrategy getStrategy() {
		return strategy;
	}

	/**
	 * Set the strategy on how to react to method changes (only has an effect on proxies created by {@link #create(Class, Object...)})
	 * @param strategy the startegy to use
	 */
	public void setStrategy(ReactiveStrategy strategy) {
		this.strategy = strategy;
	}

	public String[] getReactTo() {
		return reactTo;
	}

	/**
	 * Set the methods names to react to when using {@link ReactiveStrategy#REACT_ON_CUSTOM}
	 * @param reactTo a list of method names to react to
	 */
	public void reactTo(String... reactTo) {
		this.reactTo = reactTo;
	}

	@Override
	public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args)
	throws InvocationTargetException, IllegalAccessException {
		thisMethod.setAccessible(true);
		Object  ret   = thisMethod.invoke(backend, args);
		boolean react = strategy.reactTo(thisMethod.getName(), reactTo);
		if (react) {
			react();
		}
		return ret;
	}

	@Override
	public void bind(Observer<?> observer) {
		listeners.add(observer);
	}

	@Override
	public Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(backend);
	}

	@Override
	public void unbind(Observer<?> observer) {
		listeners.remove(observer);
	}

	@Override
	public void react() {
		listeners.forEach(Observer::update);
	}

	@Override
	public void react(String property, Object value) {
		listeners.forEach(l -> l.update(Collections.singletonMap(property, value)));
	}

	@Override
	public void set(String property, Object value) throws Throwable {
		ReactiveReflectorUtil.updateField(backend, property, value);
	}

	@Override
	public void unbindAll() {
		listeners.clear();
	}

	/**
	 * Create a new ReactiveProxy from a certain class.
	 *
	 * A Proxy manages reactivity automatically. So no need to extend ReactiveObject.<br>
	 *     This function uses the constructor of the given type. So the type <b>MUST</b> have an accessible constructor. The constructor is allowed to have arguments
	 *
	 * @param type The type the ReactiveProxy should emulate (eg. Person.class)
	 * @param constructorArgs the arguments to pass to the constructor
	 * @param <M> the type the Proxy will emulate
	 * @return the created proxy
	 */
	public static<M> ReactiveProxy<M> create(Class<M> type,Object... constructorArgs){
		return ReactiveObject.create(type,constructorArgs);
	}
}
